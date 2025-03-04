#include <stddef.h>
#include <stdlib.h>
#include <stdio.h>
#include <sqlite3.h>
#include <string.h>
#include <limits.h>
#include "dal.h"

const char *table_users = "users";
static sqlite3 *db;

int dal_init()
{
    int err = sqlite3_open("/usr/app/web_api.db", &db);
    if (err) {
        db = NULL;
        return 1;
    }

    return 0;
}

int init_users(struct User **users, int count)
{
    for (int i = 0; i <= count; i++ ) {
        struct User *user = (struct User*)malloc(sizeof(struct User));
        user->id = INVALID_USER_ID;
        *users = user;
        users++;
    }

    return 0;
}

int users_callback(void *arg, int argc, char **argv, char **column)
{
    if (argc == 3) {
        struct User **users = (struct User**)arg;
        while ((*users)->id != INVALID_USER_ID)
            users++;
        (*users)->id = atoi(argv[0]);
        strncpy((*users)->name, argv[1], NAME_SIZE);
        (*users)->age = atoi(argv[2]);
        return 0;
    }

    return 1;
}

int user_callback(void *arg, int argc, char **argv, char **column)
{
    if (argc == 3) {
        struct User *user = (struct User*)arg;
        user->id = atoi(argv[0]);
        strncpy(user->name, argv[1], NAME_SIZE);
        user->age = atoi(argv[2]);
        return 0;
    }

    return 1;
}

int dal_get_users(struct User **users, int count)
{
    if (db == NULL) return 1;
    init_users(users, count);

    char sql[BUFFER_SIZE];
    sprintf(sql, "SELECT id, name, age FROM %s LIMIT %i", table_users, count);
    char *errmsg;
    int err = sqlite3_exec(db, sql, users_callback, (void*)users, &errmsg);
    if (err) {
        printf("%s: errmsg = %s\n", __func__, errmsg);
        return 1;
    }

    return 0;
}

int dal_get_user(struct User *user)
{
    if (db == NULL) return 1;

    int id = user->id;
    user->id = INVALID_USER_ID;

    char sql[BUFFER_SIZE];
    sprintf(sql, "SELECT id, name, age FROM %s where id = %i", table_users, id);
    char *errmsg;
    int err = sqlite3_exec(db, sql, user_callback, (void*)user, &errmsg);
    if (err) {
        printf("%s: errmsg = %s\n", __func__, errmsg);
        return 1;
    }

    return 0;
}

int dal_insert_user(struct User *user)
{
    if (db == NULL) return 1;

    char sql[BUFFER_SIZE];
    sprintf(sql, "INSERT INTO %s (name, age) VALUES ('%s', %i)", table_users, user->name, user->age);
    char *errmsg;
    int err = sqlite3_exec(db, sql, NULL, (void*)user, &errmsg);
    if (err) {
        printf("%s: errmsg = %s\n", __func__, errmsg);
        return 1;
    }

    int row_count = sqlite3_changes(db);
    if (row_count == 0) return 1;

    sqlite3_int64 row_id = sqlite3_last_insert_rowid(db);
    if (row_id <= INT_MAX)
        user->id = (int)row_id;

    return 0;
}

int dal_update_user(struct User *user)
{
    if (db == NULL) return 1;

    char sql[BUFFER_SIZE];
    sprintf(sql, "UPDATE %s SET name = '%s', age = %i WHERE id = %i", table_users, user->name, user->age, user->id);
    char *errmsg;
    int err = sqlite3_exec(db, sql, NULL, (void*)user, &errmsg);
    if (err) {
        printf("%s: errmsg = %s\n", __func__, errmsg);
        return 1;
    }

    int row_count = sqlite3_changes(db);
    if (row_count == 0)
        user->id = INVALID_USER_ID;

    return 0;
}

int dal_delete_user(struct User *user)
{
    if (db == NULL) return 1;

    char sql[BUFFER_SIZE];
    sprintf(sql, "DELETE FROM %s where id = %i", table_users, user->id);
    char *errmsg;
    int err = sqlite3_exec(db, sql, user_callback, (void*)user, &errmsg);
    if (err) {
        printf("%s: errmsg = %s\n", __func__, errmsg);
        return 1;
    }

    int row_count = sqlite3_changes(db);
    if (row_count == 0)
        user->id = INVALID_USER_ID;

    return 0;
}
