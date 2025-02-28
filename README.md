# WebApi
Basic REST APIs using different frameworks and technologies.

## Frameworks + Technologies
> - C
>     - [cJSON](https://github.com/DaveGamble/cJSON)
> - Apache 2.4
> - SQLite3

## Building + Running
`docker compose up -d --build`

### REST APIs
```json
GET /ping
200 OK
Healthy
```
```json
GET /users
200 OK
[
    {
        "id": number,
        "name": string,
        "age": number
    },
    ...
]
```
```json
GET /users/{id}
200 OK
{
    "id": number,
    "name": string,
    "age": number
}
404 Not Found
```
```json
POST /users
{
    "name": string,
    "age": number
}
201 Created
{
    "id": number,
    "name": string,
    "age": number
}
```
```json
PUT /users/{id}
{
    "name": string,
    "age": number
}
200 OK
{
    "id": number,
    "name": string,
    "age": number
}
404 Not Found
```
```json
DELETE /users/{id}
204 No Content
404 Not Found
```