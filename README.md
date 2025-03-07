# WebApi
Basic REST APIs using different frameworks and technologies.

### Docker Build
`docker compose build`

### Docker Run
`docker compose up`

### REST APIs
```
GET /ping
200 OK
Healthy
```
```
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
```
GET /users/{id}
200 OK
{
    "id": number,
    "name": string,
    "age": number
}
404 Not Found
```
```
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
```
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
```
DELETE /users/{id}
204 No Content
```