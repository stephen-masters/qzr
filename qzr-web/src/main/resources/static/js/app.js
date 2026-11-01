// The HR max quiz page. Plain JavaScript with no framework and no build step:
// it calls the quiz's REST API, renders the questions, answers, knowns and
// max HR, and lists rule activations streamed over STOMP on a WebSocket.

const API = '/api/quizzes/health';

const model = {
  questions: [],
  knowns: [],
  hrmax: null,
  events: [],
};

// ---------------------------------------------------------------------------
// REST API. The quiz lives in a session-scoped service, so every request
// relies on the session cookie, which fetch sends for same-origin requests.

async function getJson(path) {
  const response = await fetch(API + path, { headers: { Accept: 'application/json' } });
  if (!response.ok) {
    throw new Error(`${path}: ${response.status}`);
  }
  return response.json();
}

async function loadQuestions() {
  model.questions = await getJson('/questions');
  try {
    model.hrmax = await getJson('/results/hrmax');
  } catch {
    model.hrmax = null;
  }
  model.knowns = await getJson('/knowns');
  render();
}

async function send(method, path, body) {
  const options = { method };
  if (body !== undefined) {
    options.headers = { 'Content-Type': 'application/json' };
    options.body = JSON.stringify(body);
  }
  const response = await fetch(API + path, options);
  if (response.ok) {
    await loadQuestions();
  } else {
    console.log(`${method} ${path}: ${response.status}`);
  }
}

function answer(question, value) {
  console.log(`Answering : ${question.key}=${value}`);
  return send('PUT', `/questions/${encodeURIComponent(question.key)}/answer`, {
    key: question.key,
    value,
  });
}

function skip(question) {
  console.log(`Skipping : ${question.key}`);
  return send('POST', `/questions/${encodeURIComponent(question.key)}/skip`);
}

function retractAnswer(question) {
  console.log(`Retracting : ${question.key}`);
  return send('DELETE', `/questions/${encodeURIComponent(question.key)}/answer`);
}

// ---------------------------------------------------------------------------
// Rendering. Everything from the server is set as text, never as markup.

function element(tag, attributes = {}, ...children) {
  const el = document.createElement(tag);
  for (const [name, value] of Object.entries(attributes)) {
    if (name.startsWith('on')) {
      el.addEventListener(name.slice(2), value);
    } else {
      el.setAttribute(name, value);
    }
  }
  el.append(...children);
  return el;
}

function button(label, onclick) {
  return element('button', { type: 'button', class: 'btn', onclick }, label);
}

function isAnswered(question) {
  return question.answer != null;
}

function renderNewQuestion(question) {
  let input;
  let value;
  if (question.answerType === 'LOCAL_DATE') {
    input = element('input', { type: 'text', class: 'form-control', 'aria-label': question.text });
    value = () => input.value;
  } else {
    input = element(
      'select',
      { class: 'form-control', 'aria-label': question.text },
      element('option', { value: '' }, '--- choose ---'),
      ...(question.options || []).map((opt) => element('option', { value: opt.key }, opt.text)),
    );
    value = () => input.value || undefined;
  }
  return element(
    'div',
    { class: 'panel-body' },
    element('p', {}, question.text),
    element('p', {}, input),
    button('Skip', () => skip(question)),
    ' ',
    button('Answer', () => answer(question, value())),
  );
}

function renderAnsweredQuestion(question) {
  const value = question.answer.value;
  return element(
    'div',
    { class: 'panel-body' },
    element('p', {}, question.text),
    element('p', {}, value != null ? value : 'Skipped'),
    button('Retract', () => retractAnswer(question)),
  );
}

function render() {
  const unanswered = model.questions.filter((q) => !isAnswered(q));
  const answered = model.questions.filter(isAnswered);

  const newQuestions = document.getElementById('new-questions');
  if (unanswered.length === 0) {
    newQuestions.replaceChildren(element('div', { class: 'panel-body' }, element('p', {}, 'No more questions')));
  } else {
    newQuestions.replaceChildren(...unanswered.map(renderNewQuestion));
  }

  document.getElementById('answers-panel').hidden = answered.length === 0;
  document.getElementById('answers').replaceChildren(...answered.map(renderAnsweredQuestion));

  document.getElementById('hrmax').textContent =
    model.hrmax != null ? `${model.hrmax.value} (${model.hrmax.algorithm})` : 'Unknown';

  document.getElementById('knowns-panel').hidden = model.knowns.length === 0;
  document
    .getElementById('knowns')
    .replaceChildren(...model.knowns.map((k) => element('li', {}, `${k.key} - ${k.value}`)));

  renderEvents();
}

function renderEvents() {
  document.getElementById('events-panel').hidden = model.events.length === 0;
  document
    .getElementById('events')
    .replaceChildren(...model.events.slice().reverse().map((e) => element('li', {}, e.name)));
}

// ---------------------------------------------------------------------------
// Rule activations. The server's STOMP endpoint is /drools, with a SockJS
// fallback; SockJS also serves a plain WebSocket at /drools/websocket, and
// STOMP's text frames are simple enough to speak directly.

function stompFrame(command, headers = {}, body = '') {
  const lines = Object.entries(headers).map(([name, value]) => `${name}:${value}`);
  return [command, ...lines, '', body].join('\n') + '\0';
}

function parseStompFrame(text) {
  const [head, ...rest] = text.split('\n\n');
  const [command, ...headerLines] = head.split('\n');
  const headers = Object.fromEntries(
    headerLines.map((line) => [line.slice(0, line.indexOf(':')), line.slice(line.indexOf(':') + 1)]),
  );
  return { command, headers, body: rest.join('\n\n') };
}

function connectEvents() {
  const scheme = location.protocol === 'https:' ? 'wss:' : 'ws:';
  const socket = new WebSocket(`${scheme}//${location.host}/drools/websocket`);

  socket.addEventListener('open', () => {
    socket.send(stompFrame('CONNECT', { 'accept-version': '1.2', host: location.hostname, 'heart-beat': '0,0' }));
  });

  socket.addEventListener('message', (message) => {
    for (const text of String(message.data).split('\0')) {
      if (text.trim() === '') continue; // heart-beat
      const frame = parseStompFrame(text.replace(/^\n+/, ''));
      if (frame.command === 'CONNECTED') {
        console.log('Connected to /drools');
        socket.send(stompFrame('SUBSCRIBE', { id: 'sub-0', destination: '/queue/agendaevents/' }));
      } else if (frame.command === 'MESSAGE') {
        model.events.push(JSON.parse(frame.body));
        renderEvents();
      } else if (frame.command === 'ERROR') {
        console.log(`STOMP error: ${frame.headers.message}`);
      }
    }
  });

  socket.addEventListener('close', () => console.log('Disconnected from /drools'));
}

connectEvents();
loadQuestions();
