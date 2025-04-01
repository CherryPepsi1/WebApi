const userService = require('../services/user-service');
const successResponses = require('../models/success-responses');
const errorResponses = require('../models/error-responses');
const { NotFoundError } = require('../errors/not-found-error');

const errorHandler = (res, err) => {
  if (err instanceof TypeError) {
    errorResponses.sendBadRequest(res, err.message);
  } else if (err instanceof NotFoundError) {
    errorResponses.sendNotFound(res);
  } else {
    console.error("Error: ", err);
    errorResponses.sendInternalServerError(res);
  }
}

const getUsers = async (req, res) => {
  try {
    const users = await userService.getUsers(req.query?.page);
    successResponses.sendOk(res, users);
  } catch (err) {
    errorHandler(res, err);
  }
};

const getUser = async (req, res) => {
  try {
    const user = await userService.getUser(req.params.id);
    successResponses.sendOk(res, user);
  } catch (err) {
    errorHandler(res, err);
  }
};

const createUser = async (req, res) => {
  try {
    const user = await userService.createUser(req.body);
    successResponses.sendCreated(res, user);
  } catch (err) {
    errorHandler(res, err);
  }
}

const updateUser = async (req, res) => {
  try {
    const user = await userService.updateUser(req.params.id, req.body);
    successResponses.sendOk(res, user);
  } catch (err) {
    errorHandler(res, err);
  } 
}

const deleteUser = async (req, res) => {
  try {
    await userService.deleteUser(req.params.id);
    successResponses.sendNoContent(res);
  } catch (err) {
    errorHandler(res, err);
  } 
}

module.exports = {
  getUsers,
  getUser,
  createUser,
  updateUser,
  deleteUser
};