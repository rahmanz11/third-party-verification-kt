**Auth Service (Login)**
API URL: https://xyz.com/partner-service/rest/auth/login
Request Type: POST
Parameter Content Type: application/json

Sample Request
{
"username": "string",
"password": "string"
}

Sample Response
{
    "status": "OK",
    "statusCode": "SUCCESS",
    "success": {
        "data": {
            "username": "demo_user",
            "access_token":
            "eyJ0eXAiOiJhY2Nlc3NfdG9rZW4iLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZmlzX3VzZXIiLCJyb2xlcyI6W
            yJBRklTX1ZFUklGSUNBVElPTiJdLCJpYXQiOjE1NjY4MjQxNTIsImV4cCI6MTU2NjgyNzcMn0.vrbtvUDD
            RyREJ0xKmK5auqc0N_6cMsEbmb221K8VbDs",
            "refresh_token":
            "eyJ0eXAiOiJyZWZyZXNoX3Rva2VuIiwiYWxnIjoiSFMyNTYifQ.eyJzdWIiOiJhZmlzX3VzZXIiLCJpYXQi
            OjE1NjY4MjQxNTIsImV4cCI6MTU2NjgzMTM1Mn0.J6lAxUyWefXTSmxmzivj0CGu0agxy6C1qKVzAcf2
            AWA"
        }
    }
}

Response Code and Messages

Tag: success
- Code: 200
- Message: Successful
Tag: error
- Code: 201
- Message: Created
- Code: 400
- Message: Bad Request
- Code: 401
- Message: You are not authorized to view the resource
- Code: 403
- Message: Accessing the resource you were trying to reach is forbidden
- Code: 404
- Message: The resource you were trying to reach is not found
- Code: 500
- Message: Internal Server Error

Tag: result
- If the operation was successful, this field will have the result.
1. Access token
2. Refresh token
3. Username

** Mark that validity of the access token for login authentication is 12 hrs. You should not call
the Login API each time or frequently rather use the access token until expired.

**Auth Service (Logout)**

API URL: https://prportal.nidw.gov.bd/partner-service/rest/auth/logout
Request Type: POST
Parameter Content Type: application/json
Request Header: Bearer Token
Sample Response
{
    "status": "OK",
    "statusCode": "SUCCESS",
    "success": {
    "data": "Logout Successful"
}
}
Response Code and Messages
Tag: success
- Code: 200
- Message: Successful
Tag: error
- Code: 201
- Message: Created
- Code: 400
- Message: Bad Request
- Code: 401
- Message: You are not authorized to view the resource
- Code: 403
- Message: Accessing the resource you were trying to reach is forbidden
- Code: 404
- Message: The resource you were trying to reach is not found
- Code: 500
- Message: Internal Server Error

Tag: result
- If the operation was successful, this field will have the result.
1. Logout success message.

Verification API

API URL: https://xyz.com/partner-service/rest/demographic/verification
Request Type: POST
Parameter Content Type: application/json
Request Header: Bearer Token

Sample Request
{"identify":
{"nid10Digit": "5090424675","nid17Digit": null},
"verify": {
"nameEn": "Abdus Salam",
"name": "আবদুস সালাম",
"dateOfBirth": " 1978-03-23",
"father": "আবদুস সাত্তার",
"mother": "উম্মে হাবীবা",
"spouse": "",
"permanentAddress": {"division": "চট্টগ্রাম","district": "চট্টগ্রাম","upozila": " ববায়ালখালী"},
"presentAddress": {"division": "ঢাকা","district": "ঢাকা","upozila": "উত্তরা"}
}
}

Or

{"identify":
{"nid17Digit": "19722699501359687", "nid10Digit": null},
"verify":{
"nameEn": "Abdus Salam Osmani",
"name": "আবদুস সালাম",
"dateOfBirth": " 1978-03-23",
"father": "আবদুস সাত্তার",
"mother": "উম্মে হাবীবা",
"spouse": "",
"permanentAddress": {"division": "চট্টগ্রাম","district": "চট্টগ্রাম","upozila": " ববায়ালখালী"},
"presentAddress": {"division": "ঢাকা","district": "ঢাকা","upozila": "উত্তরা"}
}
}

Sample Successful Response

Status Code: 200
{
"status": "OK",
"statusCode": "SUCCESS",
"success": {
"data": {
"requestId": "01e9f4ad-8888-8888-8888-828b982e0a49",
"nationalId": "9581877375",
"pin": "19722699501359634",
"photo": "https://xyz.com/file-bb/b/6/1/bfbc0027-3d5f-4c79-a494-
e21afbee774b/Photo-bfbc0027-3d5f-4c79-a494-e21afbee774b.jpg?X-Amz-Algorithm=AWS4-
HMAC-SHA256&X-Amz-Credential=fileobj%2F20250810%2Fus-east-1%2Fs3%2Faws4_request&X-
Amz-Date=20250810T055529Z&X-Amz-Expires=120&X-Amz-SignedHeaders=host&X-Amz-
Signature=5994677be4531b65225639a333e5de861224a573896bf0e303e4fbe64fe3e1a5",
}
},
"verified": true,
"fieldVerificationResult": {
"nameEn": true,
"name": true,
"dateOfBirth": true,
" father ": true,
" mother ": true
}
}


Sample Error Response
If 100% requested fields does not match but English Name matches, returning the Photo and NID
No mapping
Status Code: 406
{
"message": "Person data mismatch",
"status": "NOT_ACCEPTABLE",
"verified": "false",
"fieldVerificationResult": {
"nameEn": true,
"name": true,
"dateOfBirth": false,
" father ": false,
" mother ": true
},
"partialResponse": {
"requestId": "01e9f4ad-8888-8888-8888-828b982e0a49",
"nationalId": "9581877375",
"pin": "19722699501359634",
"photo": "https://xyz.com/file-bb/b/6/1/bfbc0027-3d5f-4c79-a494-
e21afbee774b/Photo-bfbc0027-3d5f-4c79-a494-e21afbee774b.jpg?X-Amz-Algorithm=AWS4-
HMAC-SHA256&X-Amz-Credential=fileobj%2F20250810%2Fus-east-1%2Fs3%2Faws4_request&X-
Amz-Date=20250810T055529Z&X-Amz-Expires=120&X-Amz-SignedHeaders=host&X-Amz-
Signature=5994677be4531b65225639a333e5de861224a573896bf0e303e4fbe64fe3e1a5"
}
}

If all requested fields match except English Name it will not return the Photo and NID No
mapping
Status Code: 406
{
"message": "Person data mismatch",
"status": "NOT_ACCEPTABLE",
"verified": "false",
"fieldVerificationResult": {
"nameEn": false,
"name": true,
"dateOfBirth": true,
" father ": true,
" mother ": true
}
}

If the API permission is not provided
Status Code: 403
{
"status": "FORBIDDEN",
"statusCode": "ERROR",
"error": {
"field": “SLA is not Assigned”
}
}

If any mandatory parameter is missing
Status Code: 400
{
"status": "BAD_REQUEST",
"statusCode": "ERROR",
"error": {
"field": “nid10Digit/nid17Digit”
,
"message": “Search permission without one of the mandatory fields is not allowed”
}
}

If search permission with this field is not allowed
Status Code: 400
{
"status": "BAD_REQUEST",
"statusCode": "ERROR",
"error": {
"field": “blood/gender”,
"message": “Search permission with this fields is not allowed”
}
}

If no field is provided for verification (in the verify block)
Status Code: 400
{
"status": "BAD_REQUEST",
"statusCode": "ERROR",
"error": {
"field": “verify”,
"message": “Please provide person information for verification”
}
}

Response Code and Messages
Tag: success
- Code: 200
- Message: Successful
Tag: error
- Code: 201
- Message: Created
- Code: 401
- Message: You are not authorized to view the resource
- Code: 403
- Message: Accessing the resource you were trying to reach is forbidden
- Code: 404
- Message: The resource you were trying to reach is not found
- Code: 500
- Message: Internal Server Error

Consequences of Password Expiration
Users will receive a notification as mentioned in the 'Sample Response’ when their password
expires. This response applies to all APIs, and users must reset their password through the
"Change Password" API.
Sample Response
{
"status": "UNAUTHORIZED",
"statusCode": "ERROR",
"error": {
"field": "Authorization",
"message": "Access is denied"
}
}

Change User’s Password
API URL: https://xyz.com/partner-service/rest/common/change-user-password
Request Type: POST
Parameter Content Type: application/json
Request Header: Bearer Token

Sample Request

{
"currentPassword": "string",
"newPassword": "string",
"confirmPassword": "string"
}


Sample Response
{
"status": "BAD_REQUEST",
"statusCode": "ERROR",
"error": {
"field": "password",
"message": "Old Password and New Password Cannot Be Same"
}
}
OR
{
"status": "BAD_REQUEST",
"statusCode": "ERROR",
"error": {
"field": "confirmPassword",
"message": "Password Should Not Be Greater than 50 Character"
}
}
OR
{
"status": "BAD_REQUEST",
"statusCode": "ERROR",
"error": {
"message": "Message not readable or request body is empty"
}
}
OR
{
"status": "OK",
"statusCode": "SUCCESS",
"success": {
"data": "Password Updated Successfully"
}
}
[NOTE: Upon successful password update, the user session will terminate.]

Response Code and Messages
Tag: success
- Code: 200
- Message: Successful
Tag: error
- Code: 201
- Message: Created
- Code: 401
- Message: You are not authorized to view the resource
- Code: 403
- Message: Accessing the resource you were trying to reach is forbidden
- Code: 404
- Message: The resource you were trying to reach is not found
- Code: 500
- Message: Internal Server Error
Tag: result
- If the operation was successful, this field will have the result.

Vendor Billing
API URL: https://xyz.com/partner-service/rest/partner-billing/get-billing-report
Request Type: POST
Parameter Content Type: application/json
Request Header: Bearer Token

Sample Request

{
"startDate": "2023-03-01",
"endDate": "2023-03-31"
}

Sample Response
{
"status": "OK",
"statusCode": "SUCCESS",
"success": {
"data": {
"generatedTime": "Wed Feb 01 16:46:20 BDT 2023",
"partnerId": 30,
"totalCallCount": 172,
"totalSuccessCount": 101,
"totalFailedCount": 71,
"totalProcessingCount": 0,
"totalBill": 344.0, "partnerBillingBeanList": [
{
"partnerId": 30,
"partnerName": "ABC Bank",
"username": "abc2",
"callCount": 12,
"successCount": 7,
"failedCount": 5,
"processingCount": 0,
"bill": 24.0
},
{
"partnerId": 30,
"partnerName": "ABC Bank",
"username": "abc",
"callCount": 158,
"successCount": 94,
"failedCount": 64,
"processingCount": 0,
"bill": 316.0
},
{
"partnerId": 30,
"partnerName": "ABC Bank",
"username": "abc4", "callCount": 2,
"successCount": 0,
"failedCount": 2,
"processingCount": 0,
"bill": 4.0
}
]
}
}
}

Response Code and Messages
Tag: success
- Code: 200
- Message: Successful
Tag: error
- Code: 401
- Message: You are not authorized to view the resource
- Code: 403
- Message: Accessing the resource you were trying to reach is forbidden
- Code: 404
- Message: The resource you were trying to reach is not found
- Code: 500
- Message: Internal Server Error