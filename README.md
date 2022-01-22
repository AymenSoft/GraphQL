# GraphQL
- simple application to show an exemple of using GraphQL with Kotlin
- to get the tutorial, read <a href="https://aymenmsd.medium.com/android-kotlin-graphql-tutorial-eb0d85047ef9" target="_blank">Meduim Article</a>
# Dependencies
- start by implementing dependencies in build.gradle
```kotlin
//apollo  
implementation "com.apollographql.apollo3:apollo-runtime:3.0.0"  
implementation 'com.apollographql.apollo:apollo-android-support:1.0.0'  
// lifecycle  
implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.4.0'  
//okhttp3  
implementation 'com.squareup.okhttp3:okhttp:4.9.3'  
implementation "com.squareup.okhttp3:logging-interceptor:4.8.1"
```
- add Apollo plugin to build.gradle (must be added before kotlin plugin)
```kotlin
plugins {  
  id 'com.android.application'  
  id("com.apollographql.apollo3").version("3.0.0")  
    id 'kotlin-android'  
}
```
- in the end of build.gradle, specify the package in which the Kotlin files will be generated.
```kotlin
apollo {  
  packageName.set("com.aymen.graphql")  
}
```
# .graphqlconfig
- start configuring graphql by creating a .graphqlconfig file in app directory: 
#### app\src\main\graphql
- add the graphql configuration, and put graphql server url in Default GraphQL Endpoint:
```kotlin
{
  "name": "Untitled GraphQL Schema",
  "schemaPath": "schema.graphql",
  "extensions": {
    "endpoints": {
      "Default GraphQL Endpoint": {
        "url": "http://api.spacex.land/graphql/",
        "headers": {
          "user-agent": "JS GraphQL"
        },
        "introspect": true
      }
    }
  }
}
```
# schema.graphqls
- use Android Studio terminal to import schema.graphqls using this commande:
```kotlin
gradlew :app:downloadApolloSchema --endpoint="http://api.spacex.land/graphql/" --schema=D:\KotlinStudioProjects\GraphQL\app\src\main\graphql\schema.graphqls
```
# usersList.graphql
- to import users list from graphql server, we need to create a graphql query
- use <a href="http://api.spacex.land/graphql/" target="_blank">SpaceX GraphQL</a> to create a query and copy it
```kotlin
query UsersList($limit:Int!) {
  users(limit: $limit, order_by: {timestamp: desc}) {
    id
    name
    rocket
    timestamp
    twitter
  }
}
```
# Apollo Client Instance
- create an apollo client instance 
```kotlin
private var BASE_URL = "http://api.spacex.land/graphql/"  
  
private val httpClient : OkHttpClient by lazy {  
  val httpLoggingInterceptor = HttpLoggingInterceptor()  
    httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)  
    OkHttpClient.Builder()  
        .callTimeout(60, TimeUnit.SECONDS)  
        .readTimeout(60, TimeUnit.SECONDS)  
        .addInterceptor(httpLoggingInterceptor).build()  
}  
  
fun get(): ApolloClient {  
    return ApolloClient.Builder()  
        .serverUrl(BASE_URL)  
        .okHttpClient(httpClient)  
        .build()  
}
```
# get users list
- to get users list, use users list graphql query:
```kotlin
val response = client.query(UsersListQuery(10)).execute()
```
- to get users list from response :
```kotlin
val users = response.data?.users
```