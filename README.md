# WebApi
Basic REST APIs using different frameworks and technologies.<br>
Built on **Ubuntu 24.04**.

### Docker Build
`docker compose build`

### Docker Run
`docker compose up`

### Database Schemas
#### `users`
| `id`    | `name`       | `age`   |
| ------- | ------------ | ------- |
| int     | varchar(128) | int     |

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
400 Bad Request
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
400 Bad Request
404 Not Found
```
```
DELETE /users/{id}
204 No Content
```