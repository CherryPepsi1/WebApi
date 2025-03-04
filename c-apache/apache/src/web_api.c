#include <stdio.h>
#include <httpd.h>
#include <http_protocol.h>
#include <http_config.h>
#include <cjson/cJSON.h>
#include "dal.h"

const int MAX_USERS = 5;

static int read_body(request_rec *r, const char **rbuf, apr_off_t *size)
{
    int err;
    if ((err = ap_setup_client_block(r, REQUEST_CHUNKED_ERROR))) {
        return err;
    }

    if (ap_should_client_block(r)) {
        char argsbuffer[BUFFER_SIZE];
        apr_off_t rsize, len_read, rpos = 0;
        apr_off_t length = r->remaining;

        *rbuf = (const char *) apr_pcalloc(r->pool, (apr_size_t) (length + 1));
        *size = length;
        while((len_read = ap_get_client_block(r, argsbuffer, sizeof(argsbuffer))) > 0) {
            if((rpos + len_read) > length) {
                rsize = length - rpos;
            }
            else {
                rsize = len_read;
            }

            memcpy((char *) *rbuf + rpos, argsbuffer, (size_t) rsize);
            rpos += rsize;
        }
    }

    return 0;
}

/*
 * /ping
 */
static int ping_handler(request_rec *r)
{
    if (r->method_number != M_GET)
        return HTTP_METHOD_NOT_ALLOWED;

    ap_set_content_type(r, "text/plain");
    ap_rprintf(r, "Healthy\n");
    return OK;
}

/*
 * /users
 */
static int users_handler(request_rec *r)
{
    char *uri = r->uri;
    char *path = strtok(uri, "/");
    char *param = strtok(NULL, "/");
    char *response = "";
    int code = HTTP_NOT_IMPLEMENTED;

    // GET /users
    if (param == NULL && r->method_number == M_GET) {
        struct User **users = (struct User**)malloc((MAX_USERS + 1) * sizeof(struct User));
        struct User **begin = users;
        int err = dal_get_users(users, MAX_USERS);
        if (err)
            return HTTP_INTERNAL_SERVER_ERROR;

        cJSON *results = cJSON_CreateArray();
        for (int i = 0; i <= MAX_USERS; i++) {
            if ((*users)->id != INVALID_USER_ID) {
                cJSON *result = cJSON_CreateObject();
                cJSON *id = cJSON_CreateNumber((*users)->id);
                cJSON *name = cJSON_CreateString((*users)->name);
                cJSON *age = cJSON_CreateNumber((*users)->age);
                cJSON_AddItemToObject(result, "id", id);
                cJSON_AddItemToObject(result, "name", name);
                cJSON_AddItemToObject(result, "age", age);
                cJSON_AddItemToArray(results, result);
            }
            free(*users);
            users++;
        }

        response = cJSON_Print(results);
        code = OK;

        cJSON_Delete(results);
        free(begin);

    // POST /users
    } else if (param == NULL && r->method_number == M_POST) {
        const char *body;
        apr_off_t size;
        if (read_body(r, &body, &size))
            return HTTP_INTERNAL_SERVER_ERROR;

        cJSON *input_user = cJSON_Parse(body);
        if (input_user == NULL)
            return HTTP_BAD_REQUEST;
        cJSON *input_name = cJSON_GetObjectItemCaseSensitive(input_user, "name");
        if (!cJSON_IsString(input_name) || (input_name->valuestring == NULL))
            return HTTP_BAD_REQUEST;
        cJSON *input_age = cJSON_GetObjectItemCaseSensitive(input_user, "age");
        if (!cJSON_IsNumber(input_age) || (input_age->valueint < 0))
            return HTTP_BAD_REQUEST;

        struct User *user = (struct User*)malloc(sizeof(struct User));
        user->id = INVALID_USER_ID;
        strncpy(user->name, input_name->valuestring, NAME_SIZE);
        user->age = input_age->valueint;
        int err = dal_insert_user(user);
        if (err)
            return HTTP_INTERNAL_SERVER_ERROR;

        cJSON *result = cJSON_CreateObject();
        cJSON *id = cJSON_CreateNumber(user->id);
        cJSON *name = cJSON_CreateString(user->name);
        cJSON *age = cJSON_CreateNumber(user->age);
        cJSON_AddItemToObject(result, "id", id);
        cJSON_AddItemToObject(result, "name", name);
        cJSON_AddItemToObject(result, "age", age);

        response = cJSON_Print(result);
        code = OK;

        cJSON_Delete(result);
        free(user);

    // GET /users/{id}
    } else if (param != NULL && r->method_number == M_GET) {
        struct User *user = (struct User*)malloc(sizeof(struct User));
        user->id = atoi(param);
        int err = dal_get_user(user);
        if (err)
            return HTTP_INTERNAL_SERVER_ERROR;
        if (user->id == INVALID_USER_ID)
            return HTTP_NOT_FOUND;

        cJSON *result = cJSON_CreateObject();
        cJSON *id = cJSON_CreateNumber(user->id);
        cJSON *name = cJSON_CreateString(user->name);
        cJSON *age = cJSON_CreateNumber(user->age);
        cJSON_AddItemToObject(result, "id", id);
        cJSON_AddItemToObject(result, "name", name);
        cJSON_AddItemToObject(result, "age", age);

        response = cJSON_Print(result);
        code = OK;

        cJSON_Delete(result);
        free(user);

    // PUT /users/{id}
    } else if (param != NULL && r->method_number == M_PUT) {
        const char *body;
        apr_off_t size;
        if (read_body(r, &body, &size))
            return HTTP_INTERNAL_SERVER_ERROR;

        cJSON *input_user = cJSON_Parse(body);
        if (input_user == NULL)
            return HTTP_BAD_REQUEST;
        cJSON *input_name = cJSON_GetObjectItemCaseSensitive(input_user, "name");
        if (!cJSON_IsString(input_name) || (input_name->valuestring == NULL))
            return HTTP_BAD_REQUEST;
        cJSON *input_age = cJSON_GetObjectItemCaseSensitive(input_user, "age");
        if (!cJSON_IsNumber(input_age) || (input_age->valueint < 0))
            return HTTP_BAD_REQUEST;

        struct User *user = (struct User*)malloc(sizeof(struct User));
        user->id = atoi(param);
        strncpy(user->name, input_name->valuestring, NAME_SIZE);
        user->age = input_age->valueint;
        int err = dal_update_user(user);
        if (err)
            return HTTP_INTERNAL_SERVER_ERROR;
        if (user->id == INVALID_USER_ID)
            return HTTP_NOT_FOUND;

        cJSON *result = cJSON_CreateObject();
        cJSON *id = cJSON_CreateNumber(user->id);
        cJSON *name = cJSON_CreateString(user->name);
        cJSON *age = cJSON_CreateNumber(user->age);
        cJSON_AddItemToObject(result, "id", id);
        cJSON_AddItemToObject(result, "name", name);
        cJSON_AddItemToObject(result, "age", age);

        response = cJSON_Print(result);
        code = OK;

        cJSON_Delete(result);
        free(user);

    // DELETE /users/{id}
    } else if (param != NULL && r->method_number == M_DELETE) {
        struct User *user = (struct User*)malloc(sizeof(struct User));
        user->id = atoi(param);
        int err = dal_delete_user(user);
        if (err)
            return HTTP_INTERNAL_SERVER_ERROR;

        code = HTTP_NO_CONTENT;

        free(user);
    }

    ap_set_content_type(r, "application/json");
    ap_rprintf(r, "%s", response);
    return code;
}

static int web_api_handler(request_rec *r)
{
    if (!r->handler) return DECLINED;

    if (strcmp(r->handler, "ping-handler") == 0) {
        return ping_handler(r);
    } else if (strcmp(r->handler, "users-handler") == 0) {
        return users_handler(r);
    } else {
        return DECLINED;
    }
}

static void register_hooks(apr_pool_t *pool) 
{
    dal_init();
    ap_hook_handler(web_api_handler, NULL, NULL, APR_HOOK_LAST);
}

module AP_MODULE_DECLARE_DATA   mod_web_api =
{
    STANDARD20_MODULE_STUFF,
    NULL,            /* Per-directory configuration handler */
    NULL,            /* Merge handler for per-directory configurations */
    NULL,            /* Per-server configuration handler */
    NULL,            /* Merge handler for per-server configurations */
    NULL,            /* Any directives we may have for httpd */
    register_hooks   /* Our hook registering function */
};
