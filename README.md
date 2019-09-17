# WeatherAPI_Appliciation

**Specification**

The purpose of this assignment is to give you practical experience in persisting information and sharing
data between applications. In any multi-user system, there is a risk that sensitive information used by
one application could be inappropriately obtained by another application. For this reason, Android has
significant measures in place to ensure that an Application's data is accessible only by that application,
and any between-application communication is strictly regulated.
It is often the case that users want to utilize the data contained by one application in another
application. For example, if I just booked a flight with an airline's mobile app, I might want to share
that booking information with other people through email or instant messaging.
Mobile applications typically obtain their data through either wifi or mobile data sources. However,
transmitting data through RF communications uses battery power and potentially incurs bandwith
usage costs. For this reason, it is best to utilize network communications for volatile data, and to store
any non-volatile data in a local database.

**Requirements**

1. Your application must obtain some data from a network source.
2. Your application must store data in a local sqlite database. You are free to define your own
schema.
3. Your application must facilitate the sharing of data from your application to another.
4. Your application must allow other applications to share data with it.
5. Use appropriate Android framework components to display your data in some sort of view.
6. Provide the ability for your user to search for specific data contained in your application.
7. Your application should provide some kind of useful processing of the data. i.e. the application
needs to provide some sort of value to the user other than just storing data.
