const dataService = require('./data-service');
const dataConstants = require('../constants/data-constants');
const { NotFoundError } = require('../errors/not-found-error');
const { DataError } = require('../errors/data-error');

const MAX_USERS = 10;

const getUsers = async (page) => {
  if (page != null) {
    let pageInt = parseInt(page);
    if (!Number.isInteger(pageInt) || pageInt <= 0) {
      throw new TypeError("Page must be an integer greater than 0");
    }
    var offset = (pageInt - 1) * MAX_USERS;
  }

  return await dataService.querySelect(
    dataConstants.TABLE_USERS,
    MAX_USERS,
    offset,
    dataConstants.COLUMNS_USERS
  );
}

const getUser = async (id) => {
  var idInt = parseInt(id);
  if (!Number.isInteger(idInt)) {
    throw new TypeError("Id must be an integer");
  }

  var filter = {};
  filter[dataConstants.COLUMN_ID] = idInt;
  var user = await dataService.querySelectFirst(
    dataConstants.TABLE_USERS,
    dataConstants.COLUMNS_USERS,
    filter
  );

  if (user != null) {
    return user;
  } else {
    throw new NotFoundError();
  }
}

const createUser = async (user) => {
  if (user?.name == null) {
    throw new TypeError("Name cannot be null");
  } else {
    var ageInt = parseInt(user?.age);
    if (!Number.isInteger(ageInt) || ageInt < 0) {
      throw new TypeError("Age must be an integer greater than or equal to 0");
    }
  }

  var values = {};
  values[dataConstants.COLUMN_NAME] = user.name;
  values[dataConstants.COLUMN_AGE] = user.age;
  var user = await dataService.queryInsert(
    dataConstants.TABLE_USERS,
    values,
    dataConstants.COLUMNS_USERS
  );

  if (user != null) {
    return user;
  } else {
    throw new DataError("Insert user failed");
  }
}

const updateUser = async (id, user) => {
  var idInt = parseInt(id);
  if (!Number.isInteger(idInt)) {
    throw new TypeError("Id must be an integer");
  } else if (user?.name == null) {
    throw new TypeError("Name cannot be null");
  } else {
    var ageInt = parseInt(user?.age);
    if (!Number.isInteger(ageInt) || ageInt < 0) {
      throw new TypeError("Age must be an integer greater than or equal to 0");
    }
  }

  var values = {};
  values[dataConstants.COLUMN_NAME] = user.name;
  values[dataConstants.COLUMN_AGE] = user.age;
  var filter = {};
  filter[dataConstants.COLUMN_ID] = id;
  var user = await dataService.queryUpdate(
    dataConstants.TABLE_USERS,
    values,
    filter,
    dataConstants.COLUMNS_USERS
  );

  if (user != null) {
    return user;
  } else {
    throw new NotFoundError();
  }
}

const deleteUser = async (id) => {
  var idInt = parseInt(id);
  if (!Number.isInteger(idInt)) {
    throw new TypeError("Id must be an integer");
  }

  var filter = {};
  filter[dataConstants.COLUMN_ID] = idInt;
  await dataService.queryDelete(
    dataConstants.TABLE_USERS,
    filter
  );
}

module.exports = {
  getUsers,
  getUser,
  createUser,
  updateUser,
  deleteUser
};