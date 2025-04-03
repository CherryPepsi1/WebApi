class NotFoundError extends Error {
  constructor(message = null, options = null) {
    super(message, options);
  }
}

module.exports = { NotFoundError };