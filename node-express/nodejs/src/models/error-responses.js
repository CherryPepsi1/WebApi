const { StatusCodes, ReasonPhrases } = require('http-status-codes');

class ErrorResponse {
  error;
  message;
  constructor(error, message) {
    this.error = error;
    this.message = message;
  }
}

const BadRequest = new ErrorResponse(ReasonPhrases.BAD_REQUEST);
const NotFound = new ErrorResponse(ReasonPhrases.NOT_FOUND);
const InternalServerError = new ErrorResponse(ReasonPhrases.INTERNAL_SERVER_ERROR);

const sendBadRequest = (res, message) => {
  if (message == null) {
    res.status(StatusCodes.BAD_REQUEST)
      .json(BadRequest);
  } else {
    res.status(StatusCodes.BAD_REQUEST)
      .json(new ErrorResponse(BadRequest.error, message));
  }
}

const sendNotFound = (res, message) => {
  if (message == null) {
    res.status(StatusCodes.NOT_FOUND)
      .json(NotFound);
  } else {
    res.status(StatusCodes.NOT_FOUND)
      .json(new ErrorResponse(NotFound.error, message));
  }
}

const sendInternalServerError = (res, message) => {
  if (message == null) {
    res.status(StatusCodes.INTERNAL_SERVER_ERROR)
      .json(InternalServerError);
  } else {
    res.status(StatusCodes.INTERNAL_SERVER_ERROR)
      .json(new ErrorResponse(InternalServerError.error, message));
  }
}

module.exports = {
    ErrorResponse,
    sendBadRequest,
    sendNotFound,
    sendInternalServerError
};