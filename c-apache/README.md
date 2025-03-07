## C Apache
- C
    - [cJSON](https://github.com/DaveGamble/cJSON)
- Apache 2.4
    - [APXS](https://httpd.apache.org/docs/2.4/programs/apxs.html)
- SQLite3

### Build
`apxs -n mod_web_api -i -c -o mod_web_api.so -l sqlite3 -l cjson web_api.c dal.c`