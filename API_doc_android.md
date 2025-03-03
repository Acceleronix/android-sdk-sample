## QuecIotSdk
### Function List

|Feature |Description |Implemented Version |DMP API Version|
| --- | --- | --- | --- |
|Initialize SDK |Set private cloud and basic information| 1.11.0 | V2 |
|Initialize SDK |Set user domain and basic information| 1.11.0 | V2 |
|Set country/region code |Check if code is logged in| 1.11.0 | V2|


### II. Design Interface Services/Properties

#### QuecIotSdk Initialization
    void startWithUserDomain(Application context, @NonNull String userDomain, @NonNull String userDomainSecret, @NonNull QuecCloudServiceType cloudServiceType);
|Parameter|Required|Description|	
| --- | --- | --- | 
| context  |  Yes | Application |
| userDomain |	Yes| User domain| 
| userDomainSecret |	Yes| User domain secret| 
| cloudServiceType |Yes| Enum QuecCloudServiceType, China/Europe/North America regions | 

#### QuecIotSdk Initialization
     public void startWithQuecPublicConfigBean(Application context, QuecPublicConfigBean configBean)
|Parameter|Required|Description|	
  | --- | --- | --- | 
| context  |  Yes | Application |
| QuecPublicConfigBean |	Yes| Private cloud configuration entity class|

    
    class QuecPublicConfigBean
| Property | Type |Description |
  | --- | --- | --- |
| userDomain | String | User domain |
| userDomainSecret | String | User domain secret |
| baseUrl | String | Request URL |
| webSocketV2Url | String | Websocket URL|
| mcc | String | Mobile Country Code, e.g., China is 460|
| tcpAddr | String | MQTT direct connection address |
| pskAddr | String | MQTT PSK connection address |
| tlsAddr | String | MQTT TLS connection address |
| cerAddr | String | MQTT certificate connection address |

#### Set country/region code
    public void setCountryCode(@NonNull String countryCode) 
|Parameter|Required|Description|	
| --- | --- | --- | 
| countryCode | Yes | International code, default is domestic, pass "+86"|


## Account Management SDK

### I. Function List

|Feature |Description |Implemented Version |DMP API Version|
| --- | --- | --- | --- |
|User account management | Phone number and email registration| 1.0.0 | V2|
|User account management | Login with phone number and password or verification code, login with email and password| 1.0.0 |V2 |
|User account management | Reset password| 1.0.0 | V2|
|User account management | Get and update user information| 1.0.0 | V2|

### II. Design Interface Services/Properties

### IUserService Service


#### Get Service Object
```
UserServiceFactory.getInstance().getService(IUserService.class)

```

#### HTTP Callback Interface
```
public interface IHttpCallBack {
    public void onSuccess(String result);
    public void onFail(Throwable e);
}
public interface IResponseCallBack {
    public void onSuccess();
    public void onFail(Throwable e);
    public void onError(String errorMsg);
}
```
#### Send SMS Verification Code
```
public void sendPhoneSmsCode(String internationalCode, String phone,int type, String ssid, String stid, IHttpCallBack callback);

```

|Parameter|Required|Description|	
| --- | --- | --- | 
| internationalCode |	No|International code, default is domestic, pass "86"| 
| phone |	Yes|Phone number| 
| type |Yes| 1: Registration verification code, 2: Password reset verification code, 3: Login verification code, 4: Account cancellation| 
| ssid |No|SMS signature, pass your own, default is ""| 
| stid |No|SMS template, pass your own, default is ""| 

#### Send V2 SMS Verification Code
```
public void sendV2PhoneSmsCode(String internationalCode, String phone, int codeType, IHttpCallBack callback);
```
|Parameter|Required|Description|	
| --- | --- | --- | 
| internationalCode |	Yes | International code, default is domestic, pass "86"| 
| phone |	Yes|Phone number| 
| type |Yes| 1: Password reset, 2: Login verification code, 3: Registration verification code, 4: Account cancellation| 


#### Send Email Verification Code
```
public void sendEmailCode(String eaid, String email, String etid, int type, IHttpCallBack callback);

```
|Parameter|Required|Description|	
| --- | --- | --- | 
| eaid |	No|Email account| 
| email |	Yes|Email| 
| etid |No| Email template, pass type if not provided| 
| type |No| If etid is not provided, pass type: 1: Registration, 2: Password reset, 3: Account cancellation template| 

#### Send V2 Email Verification Code
```
public void sendV2EmailCode(String email, int emailType, IHttpCallBack callback);

```
|Parameter|Required|Description|	
| --- | --- | --- | 
| email |	Yes|Email| 
| emailType |Yes| 1: Registration verification code, 2: Password reset verification code, 3: Link email verification code, 4: Delete email link verification code| 


#### Phone Number Password Registration

```
public void phonePwdRegister(String phone,String pwd,String smsCode,
 String internationalCode,String lang,String nationality,String timezone,IHttpCallBack callback);

```
|Parameter|Required|Description|	
| --- | --- | --- | 
| phone |	Yes|Phone number| 
| pwd |	Yes|Password| 
| smsCode |Yes|SMS verification code| 
| internationalCode |No|Default is not provided or ""| 
| lang |No|	Default is not provided or ""| 
| nationality |No|Default is not provided or ""| 
| timezone |No|Default is not provided or ""| 

####  Phone Number Password Login

```
 public void phonePwdLogin(String phone, String pwd, String internationalCode, IResponseCallBack callback);

```

|Parameter|Required|Description|	
| --- | --- | --- | 
| phone |Yes|Phone number| 
| pwd |	Yes|Password| 
| internationalCode |No|Default is not provided or ""| 

#### Phone Number Verification Code Login

```
public void phoneSmsCodeLogin(String phone,String smsCode,String internationalCode,IResponseCallBack callback);

```

|Parameter|Required|Description|	
| --- | --- | --- | 
| phone |	Yes|Phone number| 
| smsCode |	Yes|Verification code| 
| internationalCode |No|Default is not provided or ""| 


#### Query User Information

```
  public void queryUserInfo(IHttpCallBack callback);

```

#### Delete User

```
 public void deleteUser(int type,IHttpCallBack callback);

```
|Parameter|Required|Description|	
| --- | --- | --- | 
| type |Yes|1-- Immediate deletion, 2-- Deletion after 7 days, default is 7 days later| 


#### Reset Password by Phone Number

```
 public void userPwdResetByPhone(String internationalCode, String code,String phone,String passWord,IHttpCallBack callback);

```
|Parameter|Required|Description|	
| --- | --- | --- | 
| phone |	Yes|Phone number| 
| code |	Yes|Verification code| 
| passWord |	Yes|Password| 
| internationalCode |No|Default is not provided or ""| 

#### Reset Password by Email

```
 public void userPwdResetByEmail(String internationalCode,String code,String email,String passWord,IHttpCallBack callback);

```

|Parameter|Required|Description|	
| --- | --- | --- | 
| email |	Yes|Email| 
| code |Yes|Email verification code| 
| passWord |Yes|Password| 
| internationalCode |No|Default is not provided or ""| 

#### Check if Phone Number is Registered

```
 public void phoneIsRegister(String internationalCode, String phone,IHttpCallBack callback);

```

####  Validate International Phone Number Format

```
 public void validateInternationalPhone(String nationalCode,String phone,IHttpCallBack callback);

```
|Parameter|Required|Description|	
| --- | --- | --- | 
| phone |	Yes|Phone number| 
| nationalCode |Yes|International code| 


#### Validate SMS Verification Code

```
public void validateSmsCode(String phone,String smsCode,String internationalCode,int isDisabled, IHttpCallBack callback);

```
|Parameter|Required|Description|	
| --- | --- | --- | 
| phone |	Yes|Phone number| 
| smsCode |Yes|Verification code| 
| internationalCode |No|Default is not provided or ""| 
| isDisabled |Yes|Whether the verification code is invalid after verification, 1: Invalid, 2: Not invalid, default is 1| 

#### Change User Phone Number

```
 public void updatePhone(String newInternationalCode,String newPhone,String newPhoneCode,
                         String oldInternationalCode,String oldPhone,String oldPhoneCode,
                            IHttpCallBack callback);

```
|Parameter|Required|Description|	
| --- | --- | --- | 
| newPhone |	Yes|New phone number| 
| oldPhone |	Yes|Old phone number| 
| newInternationalCode |Yes|New international code, pass 86 for domestic| 
| oldInternationalCode |Yes|Old international code, pass 86 for domestic| 
| newPhoneCode |	Yes|New phone number verification code| 
| oldPhoneCode |	Yes|Old phone number verification code| 


#### Update User Information

```
 //Change user address information
  public void updateUserAddress(String address ,IHttpCallBack callback);
  //Change user avatar
  public void updateUserHeadImage(String headImage ,IHttpCallBack callback);
   //Change language
  public void updateUserLanguage(int language ,IHttpCallBack callback);
  //Change nickname
  public void updateUserNickName(String nikeName ,IHttpCallBack callback);
  //Change gender
  public void updateUserSex(int sex ,IHttpCallBack callback);
  //Change timezone
  public void updateUserTimezone(int timezone ,IHttpCallBack callback);
  //Change nationality
  public void updateUserNationality(int nationality ,IHttpCallBack callback);
 

```

#### Logout

```
public void userLogout(IHttpCallBack callback);

```

#### Change User Password

```
 public void changeUserPassword(String newPwd,String oldPwd,IHttpCallBack callback);

```

|Parameter|Required|Description|	
| --- | --- | --- | 
| newPwd |	Yes| New password | 
| oldPwd |	Yes|Old password| 

#### Query Language List

```
 public void queryLanguageList(IHttpCallBack callback);

```
#### Query Country List

```
public void queryNationalityList(IHttpCallBack callback);

```
#### Query Timezone List

```
  public void queryTimezoneList(IHttpCallBack callback);
  
```

#### Send Email Registration Verification Code

```
 public void sendEmailRegisterCode(String email,IHttpCallBack callback);

```

|Parameter|Required|Description|	
| --- | --- | --- |
| email |Yes|Email| 


#### Email Password Registration

```
 public void emailPwdRegister(String code,String email,String password,int lang,int nationality,int timezone,IHttpCallBack callback);

```

|Parameter|Required|Description|	
| --- | --- | --- | 
| code |Yes|Email verification code|  
| email |Yes|Email| 
| password |Yes|Password| 
| lang |Yes|Language, default is 0|
| nationality |Yes|Country, default is 0| 
| timezone |Yes|Timezone, default is 0|

#### Email Password Login

```
 public void emailPwdLogin(String email,String password,IResponseCallBack callback);

```

|Parameter|Required|Description|	
| --- | --- | --- | 
| email |Yes|Email|  
| password |Yes|Password| 


#### Send Email Password Reset Verification Code

```
  public void sendEmailRepwdCode(String email,IHttpCallBack callback);

```

|Parameter|Required|Description|	
| --- | --- | --- | 
| email |Yes| Email | 

#### Validate User Sent Email Verification Code

```
  public void validateEmailCode(String email,String code,int isDisabled, IHttpCallBack callback);

```

|Parameter|Required|Description|	
| --- | --- | --- | 
| email |Yes| Email | 
| code |Yes| Verification code | 
| isDisabled |Yes| Whether the verification code is invalid after verification, 1: Invalid, 2: Not invalid, default is 1 (Note: This interface only validates the email verification code sent when canceling the user)| 



#### Get User Token

```
  public String getToken();

```

#### Clear Local Token

```
 public void clearToken();

```

#### One-click Login with Phone Number

```
  public void phoneOneKeyLogin(OneKeyBean oneKeyBean, IHttpCallBack callback);
  Refer to the mobile document http://dev.10086.cn/dev10086/pub/loadAttach?attachId=6EF75FD09D4F40D1973CB7C36C3DB2E2
```
|Parameter|Required|Description|	
| --- | --- | --- |
| appid |Yes| Application ID applied for in unified authentication | 
| version |Yes| 2.0 | 
| strictcheck |Yes| Temporarily fill in "0", fill in "1" to perform strict verification on the server IP whitelist (IP strict verification will be mandatory in the future)| 
| token |Yes| Business credential| 
| sign |Yes| Request signature appid+version+msgid+systemtime+strictcheck+oneKeyBean.getToken()+appSecret MD5 byte to hexadecimal | 
| userDomain |Yes| User domain| 
| systemtime |Yes| System time when the request message is sent, accurate to milliseconds, a total of 17 digits, format: 20121227180001165 | 
| msgid |Yes| Random number to identify the request (1-36 digits) | 


#### User Message List

```
  public void queryUserMessageList(String pk,String dk,int msgType, int page,int pageSize,boolean isRead, String content,String title,IHttpCallBack callback);
```
|Parameter|Required|Description|	
| --- | --- | --- |
| pk |No| Product key| 
| dk |No| Device key| 
| msgType |No|Message type 1-Device notification 2-Device alarm 3-Device fault 4-System message| 
| page |No| | 
| pageSize |No|  | 
| isRead |Yes| Whether it is read| 
| content |Yes|Query content | 
| title |Yes|Query title | 

#### Read Message

```
 public void userReadMessage(String msgIdList,int msgType,IHttpCallBack callback);
```
|Parameter|Required|Description|	
| --- | --- | --- |
| msgIdList |No| List of message IDs to be read, multiple IDs are separated by commas, if not provided, all messages will be read| 
| msgType |No| Message type| 


#### Delete Message

```
 public void userDeleteMessage(String msgId,String language,IHttpCallBack callback);
```
|Parameter|Required|Description|	
| --- | --- | --- |
| msgId |Yes| Message ID| 
| language |Yes| Not required, pass ""| 


#### Query User Message Type

```
 public void queryUserMessageType(IHttpCallBack callback);
 {"code":200,"msg":"","data":"1,2,3"}
```

#### Set User Message Type

```
 public void setUserMessageType(String recvMsgPushType, IHttpCallBack callback);
```
|Parameter|Required|Description|	
| --- | --- | --- |
| recvMsgPushType |Yes| 1-Device notification 2-Device alarm 3-Device fault 4-System message, the received message types and any combination, multiple types are separated by commas| 


|HTTP Response Code|	Value|Description|	
| --- | --- | --- | 
| code |200|Success| 
| code |5032|token_invalid, code returns 5032, please exit the current page, actively jump to the login page, let the user re-acquire the token|
| code |5106|Please enter the token, the user directly calls the interface without logging in and returns this content|

#### Check if User is Logged In (Asynchronous)
```java
    checkUserLoginState(QuecCallback<Boolean> resultCallback) 
```
|Parameter|Required|Description|	
| --- | --- | --- |
| QuecResultCallback |Yes| Callback (result.successCode) true /false| 

#### Check if User is Logged In
```java
    boolean isLogin(QuecResultCallback callback)
```

#### 

```java
    public void loginByAuthCode(String authCode, QuecResultCallback<QuecResult<String>> resultCallback) 
```
|Parameter|Required|Description|	
| --- | --- | --- |
| authCode |Yes| Authorization code|
| QuecResultCallback |Yes| Callback (result.successCode) true /false| 


## Device SDK

### I. Function List

|Feature |Description |Implemented Version |DMP API Version|
| --- | --- | --- | --- |
|Device related | Device binding, device unbinding, device list, device information modification| 1.0.0 | V2|
|Device subscription related | Device subscription, unsubscribe, etc.| 1.0.0 |	V2 |
|Device control related | Device control (based on the object model)| 1.0.0 | V2|
|Device sharing related | Device sharing, unsharing, etc.| 1.0.0 | V2|

### II. Design Interface Services/Properties

### IDevService Service
#### Get Service Object
```java
DeviceServiceFactory.getInstance().getService(IDevService.class)

```

#### Bind Device Using SN
```java
 public void bindDeviceSn(String pk,String sn,String deviceName , IHttpCallBack callback);
```

|Parameter|Required|Description|	
| --- | --- | --- | 
| pk |	Yes|Device pk| 
| sn |	Yes|Device sn| 
| deviceName |	No| Default is not provided or ""| 


#### Query Device Information

```java
 public void queryDeviceInfo(String pk,String dk,String shareCode,IHttpCallBack callback);

```
|Parameter|Required|Description|	
| --- | --- | --- | 
| pk |	Yes|Product Key, with dk, or share code, choose one| 
| dk |	Yes|Device Key, with pk, or share code, choose one|
| shareCode |No|Share code, choose one with pk, dk, used by the shared person to query device information|


#### Unbind Device Using pk dk

```java
 public void unBindDevice(String pk,String dk,IHttpCallBack callback);

```

|Parameter|Required|Description|	
| --- | --- | --- | 
| pk |	Yes|Device productKey| 
| dk |	Yes|Device DeviceKey| 

#### Query User Device List

```java
public void queryUserDeviceList(String deviceName,int page,int pageSize,IHttpCallBack callback);

```
|Parameter|Required|Description|	
| --- | --- | --- | 
| page |	Yes|Page number for pagination| 
| pageSize |Yes|Number of items per page|
| deviceName |No| Device name |


#### Bind Bluetooth Device

```java
 public void bindDeviceByBlueTooth(String authCode,String pk, String dk,String pwd,IHttpCallBack callback);

```

|Parameter|Required|Description|	
| --- | --- | --- | 
| authCode |Yes|Device authCode| 
| pk |	Yes|product key|
| dk |	Yes|device key|
| pwd |	Yes|Device password| 


#### Bind WiFi Device

```java
 public void bindDeviceByWifi(String deviceName, String pk,String dk, String authCode,IHttpCallBack callback);

```

|Parameter|Required|Description|	
| --- | --- | --- | 
| deviceName |No|Default is not provided or ""|
| dk |	Yes|device key|
| pk |	Yes|product key|
| authCode |Yes|Device returned authCode| 


#### Set Device Sharing Information by Sharer

```java
 public void shareDeviceInfo(long acceptingExpireAt,String pk, String dk, int coverMark,long sharingExpireAt, IHttpCallBack callback);

```

|Parameter|Required|Description|	
| --- | --- | --- | 
| acceptingExpireAt |	Yes|Expiration time of the sharing QR code seed, timestamp (milliseconds)|
| pk |	Yes|product key|
| dk |	Yes|product key|
| coverMark |	No|Cover mark 1 Directly cover the previous valid share (default) (cover the original share code); 2 Directly add, allow multiple coexist; 3 Only when the sharing time is extended, it is allowed to cover the previous share| 
| sharingExpireAt |	No|Device usage expiration time, timestamp (milliseconds), indicating the time the shared device can be used by the shared person, if not filled, it is valid for life, only the authorizer actively unbinds| 

#### Accept Device Sharing by Shared Person

```java
  public void acceptShareDevice(String shareCode,String deviceName,IHttpCallBack callback);

```
|Parameter|Required|Description|	
| --- | --- | --- | 
| shareCode |	Yes| Share code|
| deviceName |	No| Device name|


#### Cancel Device Sharing by Shared Person

```java
 public void cancelShareByReceiver(String shareCode,IHttpCallBack callback);

```

|Parameter|Required|Description|	
| --- | --- | --- | 
| shareCode |	Yes| Share code|


#### Cancel Device Sharing by Sharer

```java
public void cancelShareByOwner(String shareCode, IHttpCallBack callback);

```

|Parameter|Required|Description|	
| --- | --- | --- | 
| shareCode |	Yes| Share code|

#### Query Shared Person List of Device by Sharer

```java
 public void getDeviceShareUserList(String pk,String dk, IHttpCallBack callback);

```

|Parameter|Required|Description|	
| --- | --- | --- |
| dk |	Yes|device key|
| pk |	Yes|product key|


#### Modify Device Information deviceName

```java
public void changeDeviceInfo(String deviceName, String pk,String dk, IHttpCallBack callback);

```
|Parameter|Required|Description|	
| --- | --- | --- |
| deviceName |	Yes|Device name|
| dk |	Yes|device key|
| pk |	Yes|product key|



#### Modify Shared Device Name by Shared Person

```java
public void changeShareDeviceName(String deviceName,String shareCode, IHttpCallBack callback);

```

|Parameter|Required|Description|	
| --- | --- | --- | 
| deviceName |	Yes|Device name | 
| shareCode |	Yes|Share code | 


#### Query Object Model TSL

```java
 public void queryProductTSL(String pk, IHttpCallBack callback);
  // Please call the SDK's buildModelListContent method to parse the TSL data structure
   List<ModelBasic> modelBasics = ObjectModelParse.buildModelListContent(jsonArray);
```

#### ~~Query Object Model TSL with Network or Local Cache~~ (Deprecated)

```java
  public void queryProductTSLWithCache(Context context, String pk, IHttpCallBack callback);
   // Please call the SDK's buildModelListContent method to parse the TSL data structure
  List<ModelBasic> modelBasics = ObjectModelParse.buildModelListContent(jsonArray);
```

|Parameter|Required|Description|	
| --- | --- | --- |
| context |	Yes| Context | 
| pk |	Yes| productKey | 

#### Query Object Model TSL with Network or Local Cache

```java
  public void getProductTSLWithCache(String pk, IDeviceTSLCallBack iDeviceTSLCallBack);
```
|Parameter|Required|Description|	
| --- | --- | --- |
| pk |	Yes| productKey | 


#### Query Device Business Attribute Values
For example, if a device defines a boolean attribute, you can query the value of that attribute as true or false, only valid for connected devices.
```java
 public void queryBusinessAttributes(List<String> codeList, String pk, String dk, List<String> typeList, String gatewayPk, String gatewayDk, IHttpCallBack callback);
 
```

|Parameter|Required|Description|	
| --- | --- | --- | 
| codeList |	No|Attributes to query; used with query types, if null, query all attributes, and typeList is also null| 
| pk |	Yes|pk|
| dk |	Yes|dk|
| gatewayPk |	No|Gateway device pk, if not provided, pass an empty string ""| 
| gatewayDk |	No|Gateway device dk, if not provided, pass an empty string ""| 
| typeList |	No| Query types 1 Query device basic attributes 2 Query object model attributes 3 Query location information| 


#### Query Device Business Object Model and Attribute Values
Query TLS and attribute values.
```java
  public void getProductTSLValueWithProductKey(String productKey, String deviceKey, String gatewayPk, String gatewayDk, List<String> codeList, List<String> typeList, IDeviceTSLModelCallback callback); 
```

|Parameter|Required|Description|	
| --- | --- | --- | 
| productKey |	Yes|productKey|
| deviceKey |	Yes|deviceKey|
| gatewayPk |	No|Gateway device pk, if not provided, pass an empty string ""| 
| gatewayDk |	No|Gateway device dk, if not provided, pass an empty string ""| 
| codeList |	No|Attributes to query; used with query types, if null, query all attributes, and typeList is also null| 
| typeList |	No| Query types 1 Query device basic attributes 2 Query object model attributes 3 Query location information| 



#### Query Device Upgrade Plan

```
 public void  queryFetchPlan(String productKey,String deviceKey,IHttpCallBack callback);
```

|Parameter|Required|Description|	
| --- | --- | --- | 
| productKey |	Yes|Device pk| 
| deviceKey |	Yes|Device dk|



#### Report Component Upgrade Status

```
public void  reportUpgradeStatus(String productKey,String deviceKey,String componentNo,int reportStatus,IHttpCallBack callback);

```

|Parameter|Required|Description|	
| --- | --- | --- | 
| productKey |	Yes|pk| 
| deviceKey |	Yes|dk|
| componentNo |	Yes|Component number returned by the query plan| 
| reportStatus |Yes|Upgrade status 0-12| 

#### Add Device Group

```
  public void addDeviceGroup(String name,IHttpCallBack callback);
```

|Parameter|Required|Description|	
| --- | --- | --- | 
| name |	Yes| Device group name| 


#### Query Device Group Details

```
 public void queryDeviceGroup(String dgid,IHttpCallBack callback);
```

|Parameter|Required|Description|	
| --- | --- | --- | 
| dgid |	Yes|Device group dgid| 


#### Modify Device Group

```
public void updateDeviceGroup(String name,String dgid,IHttpCallBack callback);
```

|Parameter|Required|Description|	
| --- | --- | --- | 
| name |	Yes|Device group name| 
| dgid |	Yes|Device group dgid| 


####  Add Device to Device Group

```
public void addDeviceToGroup(String dgid,List<AddDeviceParam> list, IHttpCallBack callback);
```

|Parameter|Required|Description|	
| --- | --- | --- | 
| dgid |	Yes|Device group dgid| 
| list |	Yes|List of devices to add| 


#### Query Device List in Device Group
```
 public void getGroupDeviceList(String dgid,String pk, int page,int pageSize,IHttpCallBack callback);
```

|Parameter|Required|Description|	
| --- | --- | --- | 
| dgid |	Yes| Device group dgid| 
| pk |	Yes|pk| 
| page |Yes|Page number| 
| pageSize |Yes|Number of items per page|


#### Remove Device from Device Group
```
public void deleteDeviceToGroup(String dgid,List<AddDeviceParam> list,IHttpCallBack callback);
```

|Parameter|Required|Description|	
| --- | --- | --- | 
| dgid |	Yes| Device group ID| 
| list |	Yes| List of devices to remove| 


#### Query Device Group List
```
 public void queryDeviceGroupList(int page,int pageSize,IHttpCallBack callback);
```

|Parameter|Required|Description|	
| --- | --- | --- | 
| page |	Yes| Page number| 
| pageSize |Yes|Number of items per page| 



#### Delete Device Group
```
  public void deleteDeviceGroup(String dgid,IHttpCallBack callback);
```

|Parameter|Required|Description|	
| --- | --- | --- | 
| dgid |	Yes| Device group ID| 


#### Query Device Group List by Device
```
 public void queryGroupListByDevice(String pk,String dk,IHttpCallBack callback);
```

|Parameter|Required|Description|	
| --- | --- | --- | 
| pk |	Yes| pk| 
| dk |	Yes|dk| 

#### Query Device List Not in Device Group
```
public void getDeviceListByNotInDeviceGroup(int page, int pageSize, String dgid, IHttpCallBack callback);
```

|Parameter|Required|Description|	
| --- | --- | --- | 
| dgid |	Yes| Device group ID| 
| page |	Yes|Current page, default is page 1| 
| pageSize |	Yes| Page size, default is 10 items| 


#### Query Child Device List under Gateway Device
```
    public void getGatewayChildList(String pk, String dk, int page, int pageSize, IHttpCallBack callback);
```
|Parameter|Required|Description|	
| --- | --- | --- | 
| pk |	Yes| product key| 
| dk |	Yes| device key| 
| page |	Yes|Current page, default is page 1| 
| pageSize |	Yes| Page size, default is 10 items| 


#### Get Device Attribute Data List
```
    public void getPropertyDataList(DeviceCodeList deviceCodeList, IHttpCallBack callback);
```
|Parameter|Required|Description|	
| --- | --- | --- | 
| attributeCode  |	Yes|  Object model attribute identifier| 
| pk |	Yes| product key| 
| dk |	Yes| device key| 
| startTimestamp  |	Yes|  Start time (millisecond timestamp)| 
| endTimestamp  |	Yes|  End time (millisecond timestamp)| 
| gatewayDk  |	No|  Gateway device Device Key| 
| gatewayPk  |	No|  Gateway device Product Key| 
| page  |	No|  Current page, default is page 1| 
| pageSize  |	No|  Page size, default is 10 items| 


#### Get Device Attribute Chart List
```
    public void getPropertyChartList(DeviceChartListBean deviceChartListBean, IHttpCallBack callback);
```
|Parameter|Required|Description|	
| --- | --- | --- | 
| attributeCode  |	Yes|  Object model attribute identifier, when querying multiple attributes, use commas to separate, up to 10 attributes| 
| pk |	Yes| product key| 
| dk |	Yes| device key| 
| startTimestamp  |	Yes|  Start time (millisecond timestamp)| 
| endTimestamp  |	Yes|  End time (millisecond timestamp)| 
| gatewayDk  |	No|  Gateway device Device Key| 
| gatewayPk  |	No|  Gateway device Product Key| 
| countType  |	No| Aggregation type (default is 3): 1-Maximum value 2-Minimum value 3-Average value 4-Difference 5-Total value| 
| timeGranularity  |	No| Statistical time granularity (default is 2): 1-Month 2-Day 3-Hour 4-Minute 5-Second| 
| timezone  |	No| Time zone offset, format: ±hh:mm| 


#### Get Device Historical Track
```
    public void getLocationHistory(TrackBean trackBean, IHttpCallBack callback);
```
|Parameter|Required|Description|	
| --- | --- | --- |
| pk |	Yes| product key| 
| dk |	Yes| device key| 
| startTimestamp  |	Yes|  Start time (millisecond timestamp)| 
| endTimestamp  |	Yes|  End time (millisecond timestamp)| 
| gatewayDk  |	No|  Gateway device Device Key| 
| gatewayPk  |	No|  Gateway device Product Key| 
| locateTypes  |	No| Location types (default is to query all types of locations): GP/GL/GA/GN/BD/PQ/LBS, when querying multiple locations, use commas to separate| 


#### Get Device Attribute Comparison Data
```
    public void getPropertyCompare(DeviceCompareBean deviceCompareBean, IHttpCallBack callback);
```
|Parameter|Required|Description|	
| --- | --- | --- | 
| attributeCode  |	Yes|  Object model attribute identifier| 
| pk |	Yes| product key| 
| dk |	Yes| device key| 
| startTimestamp  |	Yes|  Start time (millisecond timestamp)| 
| endTimestamp  |	Yes|  End time (millisecond timestamp)| 
| gatewayDk  |	No|  Gateway device Device Key| 
| gatewayPk  |	No|  Gateway device Product Key| 
| countType  |	No| Aggregation type (default is 3): 1-Maximum value 2-Minimum value 3-Average value 4-Difference 5-Total value| 
| timeGranularity  |	No| Statistical time granularity, when querying multiple granularities, use commas to separate (default is 1): 1-Day 2-Week 3-Month 4-Year| 


#### Set Device Group Sharing Information by Sharer
```
public void shareGroupInfo(long acceptingExpireAt,String dgid, int coverMark,long sharingExpireAt,IHttpCallBack callback);
```

|Parameter|Required|Description|	
| --- | --- | --- | 
| dgid |	Yes|Device group id| 
| acceptingExpireAt |	Yes|Expiration time of the sharing QR code seed, timestamp (milliseconds)| 
| coverMark |	No| Cover mark 1 Directly cover the previous valid share (default) (cover the original share code); 2 Directly add, allow multiple coexist; 3 Only when the sharing time is extended, it is allowed to cover the previous share| 
| sharingExpireAt |	No|Device usage expiration time, timestamp (milliseconds), indicating the time the shared device can be used by the shared person, if not filled, it is valid for life, only the authorizer actively unbinds| 


#### Accept Device Group Sharing by Shared Person
```
public void acceptDeviceGroupShare(String shareCode ,IHttpCallBack callback);
```

|Parameter|Required|Description|	
| --- | --- | --- | 
| shareCode |Yes| Share code| 


#### Query Accepted Device Group Details by Shared Person
```
public void queryAcceptSharedDeviceGroup(String shareCode ,IHttpCallBack callback);
```

|Parameter|Required|Description|	
| --- | --- | --- | 
| shareCode |Yes| Share code| 


#### Query Shared Person List of Device Group by Sharer
```
 public void deviceGroupShareUserList(String dgid ,IHttpCallBack callback);
```

|Parameter|Required|Description|	
| --- | --- | --- | 
| dgid |Yes| Device group id| 



#### Modify Shared Device Group Name by Shared Person

```
  public void shareUserSetDeviceGroupName(String deviceGroupName,String shareCode,IHttpCallBack callback);
```

|Parameter|Required|Description|	
| --- | --- | --- | 
| deviceGroupName |Yes| Device group name| 
| shareCode |Yes| Share code| 


#### Modify Shared Device Name in Device Group by Shared Person

```
   public void shareUserSetDeviceName(String deviceName, String pk,String dk, String shareCode,IHttpCallBack callback);
```
|Parameter|Required|Description|	
| --- | --- | --- | 
| deviceName |Yes| Device name| 
| pk |Yes| pk| 
| dk |Yes| dk| 
| shareCode |Yes| Share code| 

#### Cancel Device Group Sharing by Shared Person

```
   public void shareUserUnshare(String shareCode,IHttpCallBack callback);
```
|Parameter|Required|Description|	
| --- | --- | --- |
| shareCode |Yes| Share code| 



#### Cancel Device Group Sharing by Sharer

```
   public void owerUserUnshare(String shareCode,IHttpCallBack callback);
```
|Parameter|Required|Description|	
| --- | --- | --- |
| shareCode |Yes| Share code| 


#### Batch Control Devices

```
   public void batchControlDevice(String data, List<BatchControlDevice> deviceList, int cacheTime,int isCache,int isCover,int dataFormat, int type, IHttpCallBack callback);       
```
|Parameter|Required|Description|	
| --- | --- | --- |
| type |Yes|  1: Transparent transmission 2: Attribute 3: Service| 
| data |Yes| Attribute bool int float double enum date text" [{\"key\":\"value\"}]"  Attribute array "[{\"key\":[{\"id\":\"value1\"},{\"id\":\"value2\"}]}]" (id is 0) Attribute struct  "[{\"key\":[{\"key1\":\"value1\"},{\"key2\":\"value2\"}]}]"    Attribute array containing struct  "[{\"key\":[{\"id\":[{\"key1\":\"value1\"}]},{\"id\":[{\"key2\":\"value2\"}]}]}]" (id is 0)|
| dataFormat |Yes|Data type 1: Hex 2: Text (when type is transparent transmission, dataFormat needs to be specified) Default is 2| 
| isCache |Yes| Whether to enable cache 1: Enable 2: Do not enable, default is not enabled, HTTP downlink needs to be enabled, default is 1| 
| isCover |Yes| Whether to cover the same command sent before 1: Cover 2: Do not cover, default is not covered, this parameter is valid when cache is enabled, default is 2| 
| cacheTime |Yes| Cache time, in seconds, cache time range is 1-7776000 seconds, cache time must be set when cache is enabled| 
| deviceList |Yes| List of devices to control|


#### Add Cloud Timing

```java
  void addCornJob(CloudTiming timing, IHttpCallBack callBack);     
```
|Parameter|Required|Description|	
| --- | --- | --- |
| timing |Yes|  	Timing related parameters| 
| callBack |Yes| Callback|

CloudTiming class
|Member|	Type|Description|	
| --- | --- | --- |
| ruleId |String|  Unique identifier of the rule, required when modifying rule instance information| 
| productKey |String| Product pk|
| deviceKey |String| Device dk|
| type |String|Timing task type, once: execute once, day-repeat: repeat every day, custom-repeat: custom repeat, multi-section: multi-section execution, random: random execution, delay: delay execution (countdown)| 
| enabled |boolean| Rule status, false: stop, true: start, default: false| 
| dayOfWeek |String| When type is custom-repeat, multi-section, random, required, any combination of Monday/Tuesday/Wednesday/Thursday/Friday/Saturday/Sunday, format is "1,3,4", separated by ","| 
| timers |CloudTimingTimer| Timer list|

CloudTimingTimer class
|Member|	Type|Description|	
| --- | --- | --- |
| action |String|  JSON format, specify object model (attribute/service) + specify status| 
| time |String| When type is once, day-repeat, custom-repeat, multi-section, required, format is "HH:mm:ss", such as "12:00:00"| 
| startTime |String|Start time, when type is random, required, format is "HH:mm:ss", such as "12:00:00"| 
| endTime |String|End time, when type is random, required, and endTime must be after startTime, format is "HH:mm:ss", such as "13:00:00"| 
| delay |long| When type is delay, required, unit is s|

#### Modify Cloud Timing
```java
  void setCronJob(CloudTiming timing, IHttpCallBack callBack);
```
|Parameter|Required|Description|	
| --- | --- | --- |
| timing |Yes|  	Timing related parameters| 
| callBack |Yes| Callback|

#### Query Device Timing Task List
```java
  void getCronJobList(CloudTimingList timingList, IHttpCallBack callBack);
```
|Parameter|Required|Description|	
| --- | --- | --- |
| timingList |Yes| Timing list request parameters| 
| callBack |Yes| Callback|

CloudTimingList class
|Member|	Type|Description|	
| --- | --- | --- |
| productKey |String| Product pk|
| deviceKey |String| Device dk|
| type |String|Timing task type, if not provided, query all types; once: execute once, day-repeat: repeat every day, custom-repeat: custom repeat, multi-section: multi-section execution, random: random execution, delay: delay execution (countdown)| 
| page |int| Page number, default: 1|
| pageSize |int| Page size, default: 10|

#### Query Timing Task Details
```java
  void getCronJobInfo(String ruleId, IHttpCallBack callBack);
```
|Parameter|Required|Description|	
| --- | --- | --- |
| ruleId |Yes| Rule engine ID| 
| callBack |Yes| Callback|

#### Delete Timing Task
```java
  void batchDeleteCronJob(BatchDeleteCloudTiming batchDeleteCloudTiming, IHttpCallBack callBack);
```
|Parameter|Required|Description|	
| --- | --- | --- |
| batchDeleteCloudTiming |Yes| Rule engine ID list| 
| callBack |Yes| Callback|

BatchDeleteCloudTiming class
|Member|	Type|Description|	
| --- | --- | --- |
| ruleIdList |List<String>| Rule engine ID list|

#### Query Timing Task Limit Number under Product
```java
  void getProductCornJobLimit(String productKey, IHttpCallBack callBack);
```
|Parameter|Required|Description|	
| --- | --- | --- |
| productKey |Yes| Product pk| 
| callBack |Yes| Callback|

#### User Confirm Upgrade Plan
```java
  void userConfirmUpgrade(UpgradePlan plan, IHttpCallBack callBack);
```
|Parameter|Required|Description|	
| --- | --- | --- |
| plan |Yes| Upgrade plan parameters| 
| callBack |Yes| Callback|

UpgradePlan class
|Member|	Type|Description|	
| --- | --- | --- |
| deviceKey |String| Product dk|
| productKey |String| Product pk|
| operType |int| 1-Upgrade immediately (confirm upgrade at any time) 2-Scheduled upgrade (schedule upgrade at a specified time window) 3-(Cancel schedule and cancel upgrade)|
| appointEndTime |long| Scheduled upgrade end time (millisecond timestamp), required when operation type is 2|
| appointStartTime |long| Scheduled upgrade start time (millisecond timestamp), required when operation type is 2|

### IIotChannelControl Device Control
This class mainly includes device control related, such as device data downlink, listening to device uplink data, the underlying will automatically select the appropriate link for connection and data transmission based on the device's capability value and the current APP and device environment.


#### Get IIotChannelControl Object
```java
IotChannelController.getInstance()
```
#### Set Channel Online Status and Uplink/Downlink Data Listener
```java
IotChannelController setListener(IQuecChannelManager.IQuecCallBackListener listener);
```

#### Open Single Channel
You need to use MmkvManager.getInstance().put("uid",”****”) in advance, it is recommended to call the following method when the user logs in successfully;
```java
UserServiceFactory.getInstance().getService(IUserService.class).queryUserInfo(
       new IHttpCallBack() {
           @Override
           public void onSuccess(String result) {
               UserInfor user = new Gson().fromJson(result, UserInfor.class);
               if(user.getCode()==200)
               {
                   UserInfor.DataDTO userInfor = user.getData();
                   MmkvManager.getInstance().put("uid", userInfor.getUid());
               }
           }

           @Override
           public void onFail(Throwable e) {
               e.printStackTrace();
           }
       }
);
```
```java
public void startChannel(Context context, QuecDeviceModel pkDkModel, QuecIotDataSendMode channelMode);
```
|Parameter|Required|Description|	
| --- | --- | --- |
| context |Yes|  context| 
| pkDkModel |Yes| Device Model|
| channelMode |Yes| Channel type, QuecIotDataSendModeAuto: automatic selection, QuecIotDataSendModeWifi: wifi channel, QuecIotDataSendModeWS: ws channel, QuecIotDataSendModeBLE: Bluetooth channel|

#### Open Multiple Channels
You need to use MmkvManager.getInstance().put("uid",”****”) in advance, it is recommended to call the following method when the user logs in successfully;
```java
UserServiceFactory.getInstance().getService(IUserService.class).queryUserInfo(
       new IHttpCallBack() {
           @Override
           public void onSuccess(String result) {
               UserInfor user = new Gson().fromJson(result, UserInfor.class);
               if(user.getCode()==200)
               {
                   UserInfor.DataDTO userInfor = user.getData();
                   MmkvManager.getInstance().put("uid", userInfor.getUid());
               }
           }

           @Override
           public void onFail(Throwable e) {
               e.printStackTrace();
           }
       }
);
```
```java
public void startChannels(Context context, List<QuecDeviceModel> pkDkModels, IQuecChannelManager.IQuecCallBackListener listener);
```
|Parameter|Required|Description|	
| --- | --- | --- |
| context |Yes|  context| 
| pkDkModels |Yes| Device Model list|
| listener |No| Channel online status and uplink/downlink data listener|

#### Close Single Channel
```java
public void closeChannel(String channelId, int type);
```
|Parameter|Required|Description|	
| --- | --- | --- |
| channelId |Yes| channelId value: productKey + "_" + deviceKey| 
| type |Yes| 1: Close wifi; 2: Close WS; 3: Close Bluetooth|

#### Send Data--Read Command
```java
public void readDps(String channelId, List<QuecIotDataPointsModel.DataModel> data, @Nullable QuecIotChannelExtraData extraData, IotResultCallback callback);
```
|Parameter|Required|Description|	
| --- | --- | --- |
| channelId |Yes| channelId value: productKey + "_" + deviceKey| 
| data |Yes| dsp data|
| extraData |No| Extra data|
| callback |No| Send result callback|

#### Send Data--Write Command
```java
public void writeDps(String channelId, List<QuecIotDataPointsModel.DataModel> data, @Nullable QuecIotChannelExtraData extraData, IotResultCallback callback);
```
|Parameter|Required|Description|	
| --- | --- | --- |
| channelId |Yes| channelId value: productKey + "_" + deviceKey| 
| data |Yes| dsp data|
| extraData |No| Extra data|
| callback |No| Send result callback|

#### Get Current Channel Status
```java
public int getOnlineState(String channelId);
```
|Parameter|Required|Description|	
| --- | --- | --- |
| channelId |Yes| channelId value: productKey + "_" + deviceKey| 

#### Get Bluetooth Switch Status
```java
public int getBleState();
```

#### Close All Channels
```java
public void closeChannelAll();
```

#### Remove Device-Delete Channel
```java
public void removeDeviceChannel(String channelId);
```
|Parameter|Required|Description|	
| --- | --- | --- |
| channelId |Yes| channelId value: productKey + "_" + deviceKey| 

#### Write DPS Data Using HTTP
```java
public void writeDpsByHttp(List<QuecIotDataPointsModel.DataModel<Object>> list, List<BatchControlDevice> deviceList, int type, DpsHttpExtraDataBean extraDataBean,
                               QuecCallback<BatchControlModel> callback);
```
|Parameter|Required|Description|	
| --- | --- | --- |
| list |Yes| dps data| 
| deviceList |Yes| Device list| 
| type |Yes| Type 1: Transparent transmission 2: Attribute 3: Service| 
| extraDataBean |No| { dataFormat Data type 1: Hex 2: Text (when type is transparent transmission, dataFormat needs to be specified) cacheTime Cache time, in seconds, cache time range is 1-7776000 seconds, cache time must be set when cache is enabled isCache Whether to enable cache 1: Enable 2: Do not enable, default is not enabled isCover Whether to cover the same command sent before 1: Cover 2: Do not cover, default is not covered, this parameter is valid when cache is enabled
**View interface definition }| 
| callback |No| Result callback| 

#### Set Connecting Status Listener
```java
public void setConnectingStateListener(@NonNull QuecDeviceModel deviceModel, OnChannelConnectingStateChange change);
```
|Parameter|Required|Description|	
| --- | --- | --- |
| deviceModel |Yes| Device Model| 
| change |Yes| Status callback|

#### Remove Connecting Status Listener
```java
public void removeConnectingStateListener(@NonNull QuecDeviceModel deviceModel, OnChannelConnectingStateChange change);
```
|Parameter|Required|Description|	
| --- | --- | --- |
| deviceModel |Yes| Device Model| 
| change |Yes| Status callback|

#### Get Connecting Status Listener
```java
public int getDeviceConnectingState(@NonNull QuecDeviceModel model);
```
|Parameter|Required|Description|	
| --- | --- | --- |
| model |Yes| Device Model| 

#### Remove Channel Online Status and Uplink/Downlink Data Listener
```java
public void removeListener(IQuecChannelManager.IQuecCallBackListener listener);
```
|Parameter|Required|Description|	
| --- | --- | --- |
| listener |Yes| Channel listener| 

#### BLE Device Time Synchronization
```java
void timeZoneSync(String pk, String dk, IotResultCallback callback);
```
|Parameter|Required|Description|	
| --- | --- | --- |
| pk |Yes| Device pk| 
| dk |Yes| Device dk| 
| callback |Yes| Time synchronization result callback| 


### IWebSocketService Service
#### Get Service Object
```
WebSocketServiceLocater.getService(IWebSocketService.class)
```

#### WebSocket Login

```
  public void login();
  
```

#### Disconnect and Release

```
  public void disconnect();
  
```


#### Subscribe Device

```
   public void subscribeDevice(String deviceKey,String productKey);
  
```


#### Unsubscribe Device

```
 public void unsubscribeDevice(String deviceKey,String productKey);
  
```
#### Check if WebSocket is Open

```
  public boolean  isWebSocketOpenCallback();
  
```

####  Check if WebSocket Login is Successful

```
  public boolean  isWebSocketLoginCallback();
  
```


#### Send Basic Type Data  bool/int/float/double/enum/date/text

```
   public void writeWebSocketBaseData(KValue value, String deviceKey, String productKey);
  
     KValue data = new KValue(3,"buer", ModelStyleConstant.BOOL,"true");
     WebSocketServiceLocater.getService(IWebSocketService.class).writeWebSocketBaseData(data,dk,pk);
  
```


#### Send Array or Struct Type Data

```
     public void writeWebSocketArrayOrStructBaseData(int id, String name, List<KValue> mListChild,String dataType, String deviceKey, String productKey);
   /**
      * Parameter id is the object model attribute id
      *  name is the object model name
      *  Send array type content as basic types INT FLOAT DOUBLE TEXT
      *   mListChild content is KValue of the following types id=0 name="" The third parameter is the type, the fourth parameter is the value of the array:
      *   Boolean value can pass "false" "true"
      *    KValue  v1 = new KValue(0,"",ModelStyleConstant.INT,8);
      *    KValue  v2 = new KValue(0,"",ModelStyleConstant.INT,10);
      *     mListChild.add(v1);
      *      mListChild.add(v2);
      *     dataType Array ModelStyleConstant.ARRAY  Struct   ModelStyleConstant.STRUCT
      *
      *  Send type as Struct
      *  The first parameter is the attribute model id in the Struct The third parameter is the type The fourth parameter is the attribute value
      *        KValue  v1 = new KValue(1,"Refresh",ModelStyleConstant.BOOL,"true");
      *         KValue  v2 = new KValue(2,"Time duration",ModelStyleConstant.INT,55);
      *  mListChild.add(v1);
      *   mListChild.add(v2);
      */
    
```


#### Send Array Type Content as Struct

```
    public void writeWebSocketArrayContainStructData(int id, String name, List<KValue> mListChild, String deviceKey, String productKey);
    /**
      * 
      *  mListChild content is KValue of the following values
      *   KValue  v1 = new KValue(0,"",ModelStyleConstant.STRUCT,ChildList1);
      *   KValue  v2 = new KValue(0,"",ModelStyleConstant.STRUCT,ChildList2);
      *        mListChild.add(v1);
      *    Parameter ChildList1 value is the attribute value encapsulation in each struct
      *        KValue  v11 = new KValue(1,"Test1",ModelStyleConstant.BOOL,"true");
      *         KValue  v12 = new KValue(2,"Test2",ModelStyleConstant.ENUM,1);
      *         List<KValue> ChildList1 = new ArrayList<KValue>();
      *        ChildList1.add(v11);
      *        ChildList1.add(v12);
      */
     
```
## MQTT Channel SDK

### I. Function List

|Feature |Description |Implemented Version |Microservice Version|
| --- | --- | --- | --- |
|MQTT Communication Related | Scan peripherals, connect peripherals, send data to peripherals, receive data uploaded by peripherals | 1.0.0 | |


### II. Design Interface/Properties


#### Check if MQTT is Connected
```java
public boolean isConnected() 
```

#### Set Auto Reconnect
```java
public void setAutoReconnect(boolean isAutoReconnect)
```
|Parameter|Required|Description|	
| --- | --- | --- |
|isAutoReconnect|Yes|true is auto reconnect, false is not auto reconnect|

#### Connect MQTT
You need to use MmkvManager.getInstance().put("uid",”****”) in advance, it is recommended to call the following method when the user logs in successfully;
```java
UserServiceFactory.getInstance().getService(IUserService.class).queryUserInfo(
       new IHttpCallBack() {
           @Override
           public void onSuccess(String result) {
               UserInfor user = new Gson().fromJson(result, UserInfor.class);
               if(user.getCode()==200)
               {
                   UserInfor.DataDTO userInfor = user.getData();
                   MmkvManager.getInstance().put("uid", userInfor.getUid());
               }
           }

           @Override
           public void onFail(Throwable e) {
               e.printStackTrace();
           }
       }
);
```
```java
public void connect()
```
Connect MQTT, auto reconnect is enabled by default. If auto reconnect is not needed, you can call setAutoReconnect(false)

#### Reconnect MQTT
```java
public void reconnect() 
```

#### Subscribe Device
```java
public void subscribe(String productKey, String deviceKey)
```
|Parameter|Required|Description|	
| --- | --- | --- |
|productKey|Yes|Device productKey|
|deviceKey|Yes|Device deviceKey|
MQTT connection must be successful before calling

#### Unsubscribe Device
```java
public void unsubscribe(String productKey, String deviceKey)
```
|Parameter|Required|Description|	
| --- | --- | --- |
|productKey|Yes|Device productKey|
|deviceKey|Yes|Device deviceKey|

#### Send Message
```java
 public void sendMessage(QuecMqttMessageModel messageModel)
```
|Parameter|Required|Description|	
| --- | --- | --- |
| messageModel |Yes| QuecMqttMessageModel data|

##### class QuecMqttMessageModel
|Member|	Type|Description|	
| --- | --- | --- |
| productKey | String | Device productKey|
| deviceKey | String | Device deviceKey|
| type | String | Message type READ-ATTR Object model attribute-read; WRITE-ATTR Object model attribute-write; EXE-SERV Call object model service; EXE-SERV2 Call object model service (data protocol > 1.8); RAW Transparent command|
| kv | String | Object model downlink data|
| msgId | long | Message ID to identify the sent command, not required, maximum length 20. Used to correspond when sending a response|
| isCache | boolean | Whether to cache the downlink command|
| cacheTime | long | Downlink command cache time, in seconds|
| isCover | boolean | Whether to cover the downlink command, default is false|

Data example
```
Transparent (RAW)

{
        "isCache": true,
        "cacheTime": 3600,
        "dataFormat": "Text",
        "deviceKey": "866123456789015",
        "isCache": true,
        "productKey": "p12345",
        "raw": "123456",
        "type": "RAW"
}
Object model attribute read (READ-ATTR)

{
        "deviceKey": "1234567890",
        "kv": "[\"power\"]",
        "productKey": "p12345",
        "type": "READ-ATTR"
}
Object model attribute write (WRITE-ATTR)

{
        "deviceKey": "1234567890",
        "kv": "[{\"power\": \"true\"}]",
        "productKey": "p12345",
        "type": "WRITE-ATTR"
}
Object model service call (EXE-SERV)

{
        "productKey": "p12345",
        "deviceKey": "1234567890",
        "type": "EXE-SERV",
        "dataFormat": "Text",
        "kv": "[{\"serv\": [{\"power\":\"false\"}]}]"
}
Object model service call (EXE-SERV2)

{
        "productKey": "p12345",
        "deviceKey": "1234567890",
        "type": "EXE-SERV2",
        "dataFormat": "Text",
        "kv": "[{\"serv\": [{\"power\":\"false\"}]}]"
}
```


#### Send TTLV Data
```java
    public void sendTtlvData(String productKey, String deviceKey, byte[] ttlvData) 
```
|Parameter|Required|Description|	
| --- | --- | --- |
|productKey|Yes|Device productKey|
|deviceKey|Yes|Device deviceKey|
|ttlvData|Yes|Encoded TTLV data|

#### Set MQTT Listener
```java
public void setListener(QuecMqttListener listener)
```
##### interface QuecMqttListener

```java
public interface QuecMqttListener {

    /**
     * Connection successful
     */
    void onConnected();

    void onConnectFailed(Throwable e);

    /**
     * Connection disconnected
     */
    void onDisconnect();

    /**
     * Device subscription successful
     *
     * @param productKey Device pk
     * @param deviceKey  Device dk
     */
    void onSubscribed(String productKey, String deviceKey);

    /**
     * Device subscription failed
     *
     * @param productKey Device pk
     * @param deviceKey  Device dk
     */
    void onSubscribedFail(String productKey, String deviceKey, Throwable e);

    /**
     * Data received
     *
     * @param productKey Device pk
     * @param deviceKey  Device dk
     * @param isTtlv     true is ttlv binary data, false is json string data
     * @param data       Data
     */
    void onData(String productKey, String deviceKey, boolean isTtlv, byte[] data);
}
```

#### Disconnect
```java
public void disconnect()
```

## Bluetooth Channel SDK

### I. Function List

|Feature |Description |Implemented Version |Microservice Version|
| --- | --- | --- | --- |
|Bluetooth Communication Related | Scan peripherals, connect peripherals, send data to peripherals, receive data uploaded by peripherals | 1.0.0 | |


### II. Design Interface/Properties


### IBleService Bluetooth Service

#### Get Service Object
```
BleServiceLocater.getService(IBleService.class)

```

#### Scan Devices, can pass name or MAC address filter, pass "" for no filter
```
   public void startScan(String name, String mac, IScanCallBack iScanCallBack) 

```


#### Stop Scan
```
public void stopScan();
```
#### Connect Device by MAC Address
```
    public void  connectDevice(String mac,IBleCallBack callBack);
    BleServiceLocater.getService(IBleService.class).connectDevice(macAddress, new IBleCallBack() {
                    @Override
                    public void onSuccess() {
                        SystemClock.sleep(300);
                        //Connection successful, set notify to listen for data
                        BleServiceLocater.getService(IBleService.class).setNotify(NOTIFY_CHARACTERISTIC_UUID, new IFeedbackCallBack() {
                            @Override
                            public void receiveData(byte[] data) {
                                String notifyContent =  StringUtils.bytesToHex(data,true);
                                System.out.println("receiveData--:"+notifyContent);

                            }
                            @Override
                            public void onFail(Throwable e) {
                                    e.printStackTrace();
                            }
                        });
                    }
                    @Override
                    public void onFail(Throwable e) {
                            e.printStackTrace();
                    }
                });

```

#### Disconnect
```
 public void  disConnect();

```

#### Release Resources, call in Activity onStop method
```
  public void release();

```

#### Discover Device Services after Connection
```
  public void findServiceCharacter(IFindServiceCallBack findServiceCallBack);
  public interface IFindServiceCallBack {
    public void onScan(String UUID,FindCharacter findCharacter);
    public void onFail(Throwable throwable);
}
```

#### setNotify or setIndicate
```
  public void setNotify(String notify_UUID, IFeedbackCallBack iFeedbackCallBack);
  public void setIndicate(String indicate_UUID, IFeedbackCallBack iFeedbackCallBack);
  public interface IFeedbackCallBack {
    public void receiveData(byte[] data);
    public void onFail(Throwable throwable);
}
```

#### Read Attribute
```
  public void read(String read_UUID, IFeedbackCallBack iFeedbackCallBack);
   BleServiceLocater.getService(IBleService.class).read(READ_UUID, new IFeedbackCallBack() {
                    @Override
                    public void receiveData(byte[] data) {
                        String str =    new String(data, StandardCharsets.UTF_8);
                        System.out.println("read-str--:"+str);
                    }
                    @Override
                    public void onFail(Throwable throwable) {

                    }
                });
  
  
```

#### Write Data to Device
```
  public void write(String write_UUID, byte[] data, IFeedbackCallBack iFeedbackCallBack);
  
  BleServiceLocater.getService(IBleService.class).write(WRITE_UUID, byte[] data, new IFeedbackCallBack() {
                    @Override
                    public void receiveData(byte[] data) {
                        System.out.println("write--success");
                    }
                    @Override
                    public void onFail(Throwable throwable) {

                    }
                });
  
```

#### Check if Device is Connected, returns true if connected
```
   public boolean isConnected(String mac);
```


#### Set Device Connection Status Callback
```
   public void setiConnectChange(IConnectChange iConnectChange);
```


#### Check if UUID Supports Notify
```
    public void isNotifiable(String uuid,INotifyCallBack iNotifyCallBack);
```


#### Check if UUID Supports Indicate
```
    public void isIndicatable(String uuid,IndicateCallBack indicateCallBack);
```


### TTLV Format Data Encoding and Decoding

### ttlv data
```java
  public  class TTLVData<T>  {
    /**
     * Data type
     *  type   Boolean  0 false  1 true   2 Numeric  3 Binary  4 Struct
     */
    //id Data identifier
    public short id;
    public short type;
    public T data;
    //Whether it is ttlv
    public boolean ttlv;
```

### ttlv Encoding
```
        public  EncodeResult startEncode(short cmd,List<TTLVData> payloads)
        TTLVData<Boolean> test1 = new TTLVData<Boolean>((short)1, (short)0, false, true) ;
        TTLVData<Boolean> test2 = new TTLVData<Boolean>((short)2, (short)1, true, true) ;
        …………
        List<TTLVData> payLoads = new ArrayList<TTLVData>();
        payLoads.add(test1);
        payLoads.add(test2);
        //ttlv format data encoding and decoding
        EncodeResult encodeResult = EncodeTools.getInstance().startEncode((short) 0x0018, payLoads);
        
```
### Decode ttlv Package
```
  DecodeTools.getInstance().packetSlice(encodeResult.getCmdData(), new IParseDataListener() {
            @Override
            public void onSuccess(ParseResultData resultData) {
                Map<Integer, ReceiveTTLVData> paramMap = resultData.getValueMap();
                Set<Map.Entry<Integer, ReceiveTTLVData>> me = paramMap.entrySet();
                for (Iterator<Map.Entry<Integer, ReceiveTTLVData>> it = me.iterator(); it.hasNext();)
                {
                    Map.Entry<Integer,ReceiveTTLVData> mapValue = it.next();
                    int key =  mapValue.getKey();
                    ReceiveTTLVData useValue = mapValue.getValue();
                    String dataType = useValue.getData().getClass().getSimpleName();
                    switch (dataType)
                    {
                        case  DataStyle.BYTE:
                            byte[] data1 = (byte[]) useValue.getData();
                            String str =  new String(data1, StandardCharsets.UTF_8);
                            System.out.println("byte str --:" + str);
                            break;
                        case  DataStyle.LONG:
                            long data2 = (Long) useValue.getData();
                            break;
                        case  DataStyle.DOUBLE:
                            double data3 = (double) useValue.getData();
                            break;

                        case  DataStyle.BOOLEAN:
                            boolean data4 = (boolean) useValue.getData();
                            break;
                            
                          case  DataStyle.Array:
                        List<ReceiveTTLVData> data5 = (List<ReceiveTTLVData>) useValue.getData();
                        for(ReceiveTTLVData rt: data5)
                        {
                            String style = rt.getData().getClass().getSimpleName();
                            if(style.equals(DataStyle.BYTE))
                            {
                                byte[] data = (byte[]) rt.getData();
                                String content =  new String(data, StandardCharsets.UTF_8);
                                System.out.println("byte--:" + content);
                            }
                            System.out.println("ReceiveTTLVData-:"+rt);
                        }
                        break;
                    }

                }


            }
            @Override
            public void onProcessing(String msg) {
                System.out.println("onProcessing--:"+msg);

            }
        });
 
 
 
```

### ~~WiFi Configuration~~ (Deprecated, use IQuecDevicePairingService)
```
1. Scan and connect to the Bluetooth device;
2. Encode WiFi name ssid and password pass in ttlv format, write data via BLE
3. Decode the returned pk, dk, authCode from the device;
4. Call the HTTP interface bindDeviceByWifi to bind the device;
example:
    byte[] bytes = ssid.getBytes(StandardCharsets.UTF_8);
    byte[] bytes2 = pass.getBytes(StandardCharsets.UTF_8);
    TTLVData<byte[]> test1 = new TTLVData<byte[]>((short) 1, (short)3, bytes, true) {
    };
    TTLVData<byte[]> test2 = new TTLVData<byte[]>((short) 2, (short)3, bytes2, true) {
    };
    List<TTLVData> list = new ArrayList<TTLVData>();
    list.add(test1);
    list.add(test2);
    //ttlv data encoding
    EncodeResult encodeResult = EncodeTools.getInstance().startEncode((short) 0x7010, list);
    //WiFi configuration, write data
    BleServiceLocater.getService(IBleService.class).write(writeUUID, encodeResult.getCmdData(), new IFeedbackCallBack()
    …………
    Bluetooth notify, decode data…… Call interface for configuration
    DeviceServiceFactory.getInstance().getService(IDevService.class).bindDeviceByWifi(String deviceName, String pk,String dk, String authCode,IHttpCallBack callback);
    
```


## Configuration SDK quec-smart-config-sdk-api

### I. Function List
|Feature |Description |Implemented Version |Microservice Version|
| --- | --- | --- | --- |
|	Device Configuration| Device configuration |	1.0.0	| |
|	Register Configuration Listener|  |	1.0.0	| |
|	Unregister Configuration Listener|  |	1.0.0	| |

### II. ~~IQuecSmartConfigService Design Interface/Properties~~ (Deprecated, use IQuecDevicePairingService)

#### ~~Start Configuration~~ (Deprecated)
```java
@Deprecated
public void startConfigDevices(@NonNull List<DeviceBean> list, @NonNull String ssid, @NonNull String password)
``` 
|Parameter|Required|Description|	
| --- | --- | --- |
| list |Yes| List of scanned devices| 
| ssid |Yes| ssid| 
| password |Yes| password| 

#### ~~Register Configuration Listener~~ (Deprecated)

```java
  @Deprecated
   public void addSmartConfigListener(QuecSmartConfigListener listener)
```

#### ~~Unregister Configuration Listener~~ (Deprecated)

```java
  @Deprecated
  public void removeSmartConfigListener(QuecSmartConfigListener listener) 
```
### III. QuecDevicePairingServiceManager


#### Initialize
```kotlin
  fun init(context: Context)
```
|Parameter|Required|Description|	
| --- | --- | --- |
|context|Yes|Context|

#### Scan Devices

Scan results refer to QuecPairingListener

```kotlin
  fun scan(fid: String?, name: String?, mac: String?)
```
|Parameter|Required|Description|	
| --- | --- | --- |
|fid|No|Family id|
|name|No|Bluetooth name|
|mac|No|Bluetooth mac address|

#### Stop Scan

```kotlin
  fun stopScan()
```
#### Start Pairing Devices

Configuration progress and results refer to QuecPairingListener

```kotlin
  fun startPairingByDevices(
        devices: MutableList<QuecPairDeviceBean>?, fid: String?, ssid: String?, pw: String?
    )
```
|Parameter|Required|Description|	
| --- | --- | --- |
|devices|Yes| Devices to be bound|
|fid|No|Family id|
|ssid|No|WiFi name|
|pw|No|WiFi password|


#### Cancel All Device Pairing

```kotlin
  fun cancelAllDevicePairing()
```

#### Set WiFi Configuration Timeout

```kotlin
  fun setWiFiPairingDuration(duration: Int): Boolean
```
|Parameter|Required|Description|	
| --- | --- | --- |
|duration|Yes|60~120, default is 120 seconds, unit: seconds|

##- return true: set successfully, false: set failed

#### Set BLE Pairing Timeout

```kotlin
  fun setBlePairingDuration(duration: Int): Boolean
```
|Parameter|Required|Description|	
| --- | --- | --- |
|duration|Yes|30~60, default is 60 seconds, unit: seconds|


#### Add Configuration Listener

```kotlin
  fun addPairingListener(listener: QuecPairingListener?)
```
|Parameter|Required|Description|	
| --- | --- | --- |
|listener|Yes|Result callback (scan device callback, configuration progress and result)|

#### Remove Configuration Listener

```kotlin
  fun removePairingListener(listener: QuecPairingListener?)
```
|Parameter|Required|Description|	
| --- | --- | --- |
|listener|Yes|Result callback)|

#### QuecPairingListener Interface

```kotlin
  interface QuecPairingListener {

    /**
     * Scan device
     * @param deviceBean Device information
     */
    fun onScanDevice(deviceBean: QuecPairDeviceBean)
    /**
     * Update pairing progress
     * @param deviceBean Device information
     * @param progress Progress
     */
    fun onUpdatePairingStatus(deviceBean: QuecPairDeviceBean, progress: Float)

    /**
     * Configuration result
     * @param deviceBean Device information
     * @param result Configuration result
     * @param errorCode Error code
     */
    fun onUpdatePairingResult(deviceBean: QuecPairDeviceBean, result: Boolean, errorCode: QuecPairErrorCode)
}
```

QuecPairDeviceBean class
|Member|	Type|Description|	
| --- | --- | --- |
| bleDevice | QuecBleDevice | Scanned BLE device object|
| deviceName | String | Device name|
| productName | String | Product name|
| productLogo | String | Product LOGO|
| bindingMode | int | Device binding mode, multi-bind: 1, unique: 2, alternate: 3|

QuecBleDevice class
|Member|	Type|Description|	
| --- | --- | --- |
| id | String |Device unique identifier|
| version | String | Firmware version|
| productKey | String | Device pk|
| deviceKey | String | Device dk|
| mac | String | Bluetooth mac address|
| isWifiConfig | Boolean | WiFi device is configured, 1 means configured, 0 means not configured|
| isBind | Boolean | Is bound|
| isEnableBind | String | Is binding allowed|
| capabilitiesBitmask | Int | Device capability value bit0=1 means the device supports WAN far-field communication capability bit1=1 means the device supports WiFi LAN near-field communication capability bit2=1 means the device supports BLE near-field communication capability|

QuecPairErrorCodes Description
|Type|	Value|Description|	
| --- | --- | --- |
|QUEC_PAIRING_WAITING|301|Device waiting to be bound|	
|QUEC_PAIRING_BLE_CONNECTING|302|Bluetooth connecting|	
|QUEC_PAIRING_BLE_CONNECTED_FAIL|303|Bluetooth connection failed|	
|QUEC_PAIRING_WIFI_GET_BINDING_CODE_FAIL|304|WiFi configuration device, timeout without getting binding code|	
|QUEC_PAIRING_WIFI_BINDING_SUCCESS|305|WiFi configuration successful|	
|QUEC_PAIRING_WIFI_BINDING_FAIL|306|WiFi configuration failed|	
|QUEC_PAIRING_BLE_GET_RANDOM_FAIL|307|Failed to get random from Bluetooth device|	
|QUEC_PAIRING_BLE_GET_ENCRYPTION_CODE_FAIL|308|Failed to request encrypted binding code from the cloud|	
|QUEC_PAIRING_BLE_CODE_AUTH_FAIL|309|Failed to authenticate with Bluetooth device|	
|QUEC_PAIRING_BLE_CODE_AUTH_SUCCESS|310|Authentication with device successful|	
|QUEC_PAIRING_BLE_BINDING_SUCCESS|311|Bluetooth binding successful|	
|QUEC_PAIRING_BLE_BINDING_FAIL|312|Bluetooth binding failed|	
|QUEC_PAIRING_FAIL|313|General exception scenario: binding failed, such as parameter issues|	


## OTA SDK

### HTTP OTA

#### Get Service Object
```kotlin
  QuecHttpOtaServiceFactory.getInstance().getService(IQuecHttpOtaService::class.java)
```

#### Check if User Has Upgradable Devices

```java
  void getUserIsHaveDeviceUpgrade(String fid, IHttpCallBack callBack);
```
|Parameter|Required|Description|	
| --- | --- | --- |
| fid |Yes| Family id| 
| callBack |Yes| Callback|

#### Query Upgradable Device List
```java
  void getUpgradePlanDeviceList(String fid, int page, int pageSize, IHttpCallBack callBack);
```
|Parameter|Required|Description|	
| --- | --- | --- |
| fid |Yes| Family id| 
| page |Yes| Page number to query, default is 1| 
| pageSize |Yes| Page size to query, default is 10| 
| callBack |Yes| Callback|

#### Query Device Upgrade Plan
```java
  void getDeviceUpgradePlan(String dk, String pk, IHttpCallBack callBack);
```
|Parameter|Required|Description|	
| --- | --- | --- |
| dk |Yes| Product dk| 
| pk |Yes| Product pk| 
| callBack |Yes| Callback|

#### Batch Confirm Upgrade
```java
  void userBatchConfirmUpgradeWithList(List<UpgradePlan> list, IHttpCallBack callBack);
```
|Parameter|Required|Description|	
| --- | --- | --- |
| list |Yes| List of upgrade plan parameters| 
| callBack |Yes| Callback|

UpgradePlan class
|Member|	Type|Description|	
| --- | --- | --- |
| version | String | Firmware version|
| productKey | String | Device pk|
| deviceKey | String | Device dk|
| operType | int | 1-Upgrade immediately (confirm upgrade at any time) 2-Scheduled upgrade (schedule upgrade at a specified time window) 3-(Cancel schedule and cancel upgrade)|
| appointStartTime | long | Scheduled upgrade start time (millisecond timestamp), required when operation type is 2|
| appointEndTime | long | Scheduled upgrade end time (millisecond timestamp), required when operation type is 2|
| planId | long | Upgrade plan ID|


#### Batch Query Device Upgrade Details
```java
  void getBatchUpgradeDetailsWithList(List<UpgradeDeviceBean> list, IHttpCallBack callBack);
```
|Parameter|Required|Description|	
| --- | --- | --- |
| list |Yes| List of upgrade plans| 
| callBack |Yes| Callback|

UpgradeDeviceBean class
|Member|	Type|Description|	
| --- | --- | --- |
| deviceKey |String| Product dk|
| productKey |String| Product pk|
| planId |long| Upgrade plan ID|


### Bluetooth OTA QuecBleOtaManager

#### Query Single Device Upgrade Plan

```kotlin
  fun checkVersion(pk: String, dk: String, callback: QuecCallback<QuecBleOtaInfo?>)
```
|Parameter|Required|Description|	
| --- | --- | --- |
| pk | Yes | Device pk|
| dk | Yes | Device dk|
|callback|Yes|Result callback|

QuecBleOtaInfo class
|Member|	Type|Description|	
| --- | --- | --- |
| pk | String | Device pk|
| dk | String | Device dk|
| targetVersion | String | New version number|
| componentNo | String | Component number|
| desc | String | Upgrade description|
| fileName | String | File name|
| fileUrl | String | File download address|
| fileSize | Int | File size|
| fileSign | String | File Hash256 value|
| planId | Int | Upgrade plan ID|

#### Upgrade Status Callback Interface

```kotlin
   fun addStateListener(listener: StateListener?)
```
|Parameter|Required|Description|	
| --- | --- | --- |
| listener | Yes | Upgrade status callback interface, triggered when OTA upgrade is successful or failed|

```kotlin
   fun addProgressListener(listener: ProgressListener?)
```
|Parameter|Required|Description|	
| --- | --- | --- |
| listener | Yes | Upgrade progress callback interface, called back during OTA upgrade, range: 0~1|

#### Start OTA Upgrade

```kotlin
   fun startOta(infoList: List<QuecBleOtaInfo>)
```
|Parameter|Required|Description|	
| --- | --- | --- |
| infoList | Yes | QuecBleOtaInfo obtained when querying the upgrade plan|


#### Stop OTA Upgrade

```kotlin
   fun stopOta(infoList: List<QuecBleOtaInfo>)
```
|Parameter|Required|Description|	
| --- | --- | --- |
| infoList | Yes | QuecBleOtaInfo obtained when querying the upgrade plan|

#### ProgressListener Interface

```kotlin
   fun interface ProgressListener {
        /**
        * Upgrade successful
        * @param pk Device pk
        * @param dk Device dk
        * @param progress Upgrade progress, range is 0.0~1.0
        */
        fun onUpdate(pk: String, dk: String, progress: Double)
    }
```

#### StateListener Interface
```kotlin
   interface StateListener {

        /**
        * Upgrade successful
        * @param pk Device pk
        * @param dk Device dk
        * @param waitTime Time required for the device to upgrade successfully
        */
        fun onSuccess(pk: String, dk: String, waitTime: Long)

        /**
        * Upgrade failed
        * @param pk Device pk
        * @param dk Device dk
        * @param errorCode Upgrade failure error code
        */
        fun onFail(pk: String, dk: String, errorCode: BleFileErrorType)
    }
```

BleFileErrorType Description
|Type|Description|	
| --- | --- |
|COMMON|General error|	
|NOT_CONNECT|Bluetooth not connected|	
|NO_FILE_PATH|Upgrade file path does not exist|	
|FILE_CHECK_FAIL|Upgrade file verification failed|	
|DEVICE_REFUSE|Device refused to upgrade|	
|DEVICE_CANCELLED|Device canceled upgrade|	
|DEVICE_FAIL|Device upgrade failed|	
|TIMEOUT|Upgrade timeout|