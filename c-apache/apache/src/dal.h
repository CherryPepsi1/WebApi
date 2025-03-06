#pragma once

#define BUFFER_SIZE 256
#define NAME_SIZE 128

extern const int INVALID_USER_ID;

struct User {
    int id;
    char name[NAME_SIZE];
    int age;
};

int dal_init();
int dal_get_users(struct User **users, int count);
int dal_get_user(struct User *user);
int dal_insert_user(struct User *user);
int dal_update_user(struct User *user);
int dal_delete_user(struct User *user);
