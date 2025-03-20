const { StatusCodes, ReasonPhrases } = require('http-status-codes');

const sendOk = (res, body) => {
  res.status(StatusCodes.OK)
    .json(body);
}

const sendCreated = (res, body) => {
  res.status(StatusCodes.CREATED)
    .json(body);
}

const sendNoContent = (res) => {
  res.status(StatusCodes.NO_CONTENT)
    .json();
}

module.exports = {
    sendOk,
    sendCreated,
    sendNoContent
};