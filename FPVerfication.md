AFIS Service Secured (Verification)
API URL: https://prportal.nidw.gov.bd/partner-service/rest/afis/verification-secured
Request Type: POST
Parameter Content Type: application/json
Request Header: Bearer Token
Parameter Name Description Type
nid10Digit 10 digit NID number provided by partner to
access detail of NID
MANDATORY
(any one)
nid17Digit 17 digit NID number provided by partner to
access detail of NID
MANDATORY
(any one)
dateOfBirth Date of Birth of a NID holder provided by
partner to access detail of NID MANDATORY
fingerEnums List of Fingers Provided by partner to upload
fingerprint MANDATORY
Sample Request
{
"dateOfBirth":"1987-03-01",
"nid10Digit": "5956750714",
"nid17Digit": "19875114390226276",
"fingerEnums":
[
"RIGHT_THUMB",
"RIGHT_INDEX",
"RIGHT_MIDDLE",
"RIGHT_RING",
"RIGHT_LITTLE",
"LEFT_THUMB",
"LEFT_INDEX",
"LEFT_MIDDLE",
"LEFT_RING",
"LEFT_LITTLE"
]
}

Sample Response
{
"status": "ACCEPTED",
"statusCode": "SUCCESS",
"success": {
"data": {
"fingerUploadUrls":
[
{
"finger": "RIGHT_THUMB",
"url": "https://prportal.nidw.gov.bd/file-
others/finger_print/MzBkUnABsGiqk1W_h_A_/right_thumb.wsq?X-Amz-Algorithm=AWS4-
HMAC-SHA256&X-Amz-Credential=filetigerit%2F20200217%2Fus-e
ast-1%2Fs3%2Faws4_request&X-Amz-Date=20200217T101336Z&X-Amz-Expires=604800&X
Amz-SignedHeaders=host&X-Amz-Signature=7925396fee5b2693766a2e4befa253ca1f51289d6
7922bbedfee01c57a1456b8"
},
{
"finger": "RIGHT_INDEX",
"url": "https://prportal.nidw.gov.bd/file-
others/finger_print/MzBkUnABsGiqk1W_h_A_/right_index.wsq?
X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=filetigerit%2F20200217%2Fus-ea
st-1%2Fs3%2Faws4_request&X-Amz-Date=20200217T101336Z&X-Amz-Expires=604800&X-
Amz-SignedHeaders=host&X-Amz-
Signature=1db18fc2840f898cf70a9a6aeea10e8344649a5abc
dbf78b4a384684c6f6966a"
},
{
"finger": "RIGHT_MIDDLE",
"url": "https://prportal.nidw.gov.bd/file-
others/finger_print/MzBkUnABsGiqk1W_h_A_/right_middle.wsq
?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=filetigerit%2F20200217%2Fus-e
ast-1%2Fs3%2Faws4_request&X-Amz-Date=20200217T101336Z&X-Amz-
Expires=604800&XAmz-SignedHeaders=host&X-Amz-
Signature=1956eaa58a130c7f9768318894dc84aea300f290
9d5f67b4b7a2b624b7fa1894"
},
{
"finger": "RIGHT_RING",
"url": "https://prportal.nidw.gov.bd/file-
others/finger_print/MzBkUnABsGiqk1W_h_A_/right_ring.wsq?X-Amz-Algorithm=AWS4-
HMAC-SHA256&X-Amz-Credential=filetigerit%2F20200217%2Fus-east-
1%2Fs3%2Faws4_request&X-Amz-Date=20200217T101336Z&X-Amz-Expires=604800&X-Amz-
SignedHeaders=host&X-Amz-
Signature=bab88485e481d4b134de024001b8c5c58e9ba4a13749b4ea93b86331663113fb"
},
{
"finger": "RIGHT_LITTLE",
"url": "https://prportal.nidw.gov.bd/file-
others/finger_print/MzBkUnABsGiqk1W_h_A_/right_little.wsq?X-Amz-Algorithm=AWS4-
HMAC-SHA256&X-Amz-Credential=filetigerit%2F20200217%2Fus-east-
1%2Fs3%2Faws4_request&X-Amz-Date=20200217T101336Z&X-Amz-Expires=604800&X-Amz-
SignedHeaders=host&X-Amz-
Signature=fcbe120afc09302d7d62dd80b39c680267d78607ff958f40c664e185e22b0dea"
},
{
"finger": "LEFT_THUMB",
"url": "https://prportal.nidw.gov.bd/file-
others/finger_print/MzBkUnABsGiqk1W_h_A_/left_thumb.wsq?X-Amz-Algorithm=AWS4-
HMAC-SHA256&X-Amz-Credential=filetigerit%2F20200217%2Fus-east-
1%2Fs3%2Faws4_request&X-Amz-Date=20200217T101336Z&X-Amz-Expires=604800&X-Amz-
SignedHeaders=host&X-Amz-
Signature=fbb1e17ed5be6b815738bd21cf293f560358fc4aaa6197116f713a96c9a9ad77"
},
{
"finger": "LEFT_INDEX",
"url": "https://prportal.nidw.gov.bd/file-
others/finger_print/MzBkUnABsGiqk1W_h_A_/left_index.wsq?X-Amz-Algorithm=AWS4-
HMAC-SHA256&X-Amz-Credential=filetigerit%2F20200217%2Fus-east-
1%2Fs3%2Faws4_request&X-Amz-Date=20200217T101336Z&X-Amz-Expires=604800&X-Amz-
SignedHeaders=host&X-Amz-
Signature=c70fc230f14127bd03d57a2610e6404ba76efd6d97127a7038d232cd1343f4e0"
},
{
"finger": "LEFT_MIDDLE",
"url": "https://prportal.nidw.gov.bd/file-
others/finger_print/MzBkUnABsGiqk1W_h_A_/left_middle.wsq?X-Amz-Algorithm=AWS4-
HMAC-SHA256&X-Amz-Credential=filetigerit%2F20200217%2Fus-east-
1%2Fs3%2Faws4_request&X-Amz-Date=20200217T101336Z&X-Amz-Expires=604800&X-Amz-
SignedHeaders=host&X-Amz-
Signature=7af235a280b162bcfab8ca5916677de5354c63afa8412b26c0076cc998600dad"
},
{
"finger": "LEFT_RING",
"url": "https://prportal.nidw.gov.bd/file-
others/finger_print/MzBkUnABsGiqk1W_h_A_/left_ring.wsq?XAmz-Algorithm=AWS4-HMAC-
SHA256&X-Amz-Credential=filetigerit%2F20200217%2Fus-east-1%2Fs3%2Faws4_request&X-
Amz-Date=20200217T101336Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-
Signature=a99f425b34a7e2798f5b653cdc008164ffc657cd3810cf5b4452395154ea6cc1"
},
{
"finger": "LEFT_LITTLE",
"url": "https://prportal.nidw.gov.bd/file-
others/finger_print/MzBkUnABsGiqk1W_h_A_/left_little.wsq?XAmz-Algorithm=AWS4-
HMAC-SHA256&X-Amz-Credential=filetigerit%2F20200217%2Fus-east-
1%2Fs3%2Faws4_request&X-Amz-Date=20200217T101336Z&X-Amz-Expires=604800&X-Amz-
SignedHeaders=host&X-Amz-Signature=ac37489cfa97a7799d68e8ade6a6f4278d46ee19cbc1de1efce3bd966f633d14"
}
],
"resultCheckApi":
"/partner-service/rest/afis/verification/result/12ba38e5-d2a0-4d31-834a-
2121f1915266"
}
}
}

Or
{
"status": "FORBIDDEN",
"statusCode": "ERROR",
"error": {
"field": "",
"message": "Voter is locked. Details cannot be fetched"
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

1. Fingerprint Upload URL with corresponding finger name
2. Result Check URL with jobId

Fingerprint Upload
NOTE: Need to upload all the fingerprints (encrypted) which were provided in the request
body.
API URL: https://prportal.nidw.gov.bd/file-
others/finger*print/MzBkUnABsGiqk1W_h_A*/right_thumb.wsq?X-Amz-Algorithm=AWS4-
HMAC-SHA256&X-Amz-Credential=filetigerit%2F20200217%2Fus-east-
1%2Fs3%2Faws4_request&X-Amz-Date=20200217T101336Z&X-Amz-Expires=604800&X-Amz-
SignedHeaders=host&X-Amz-
Signature=7925396fee5b2693766a2e4befa253ca1f51289d67922bbedfee01c57a1456b8
Request Type: PUT
Sample Request
Need to upload file from body as binary mode and file must be in WSQ format.
Response
Status: 200 OK
Result Check
API URL: https://prportal.nidw.gov.bd/partner-
service/rest/afis/verification/result/12ba38e5-d2a0-4d31-834a-2121f1915266
Request Type: GET
Request Header: Bearer Token
Sample Response
{
"status": "OK",
"statusCode": "SUCCESS",
"success": {
"data": {
"jobId": "746694e5-9c40-4ee7-846a-b6ed729ba0e5",
"result": "PROCESSING_PENDING"
}
}
}
OR
{
"status": "OK",
"statusCode": "SUCCESS",
"success": {
"data": {
"jobId": "5a0b3045-9a89-40e1-ab6e-a3687d392e34",
"result": "MATCH FOUND",
"verificationResponse": {
"voterInfo": {
"id": "3ca5d22e-2f37-4527-b3c3-325054af7534",
"nationalId": "5956750714"
}
}
}
}
}
OR
{
"status": "OK",
"statusCode": "SUCCESS",
"success": {
"data": {
"jobId": "e2ad4d1d-e28f-41e9-b576-3963ba636121",
"result": "NO MATCH FOUND",
"errorReason": "NID_OR_DOB_NOT_MATCH"
}
}
}
OR
{
"status": "OK",
"statusCode": "SUCCESS",
"success": {
"data": {
"jobId": "12ba38e5-d2a0-4d31-834a-2121f1915266",
"result": "NO MATCH FOUND",
"errorReason": "FINGER_PRINT_NOT_MATCH"
}
}
}
OR
{
"status": "UNAUTHORIZED",
"statusCode": "ERROR",
"error": {
"field": "Authorization",
"message": "Full authentication is required to access this resource"
}
}
OR
{
"status": "OK",
"statusCode": "SUCCESS",
"success": {
"data": {
"jobId": "12ba38e5-d2a0-4d31-834a-2121f1915266",
"result": "NO MATCH FOUND",
"errorReason": "FINGER_PRINT_NOT_MATCH"
}
}
}

Result Description
PROCESSING_PENDING Fingerprint uploaded. Will send request to AFIS
MATCH FOUND Will get the voter details as per SLA
NO MATCH FOUND If wrong NID or DoB provided
"errorReason": "NID_OR_DOB_NOT_MATCH"
NO MATCH FOUND Fingerprint don't match
"errorReason": "FINGER_PRINT_NOT_MATCH"
ERROR If fingerprints not found in server database
ERROR If voter is locked
