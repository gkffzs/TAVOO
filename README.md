## TAVOO
This repository contains the files for [**TAVOO**](https://play.google.com/store/apps/details?id=gr.upatras.ceid.kaffezas.tavoo), a [free software](https://www.gnu.org/philosophy/free-sw.html) Android application that I partially developed as part of my [diploma thesis](http://nemertes.lis.upatras.gr/jspui/handle/10889/8959) (as an undergraduate student of the [Computer Engineering & Informatics Department](https://www.ceid.upatras.gr/) at the [University of Patras](http://www.upatras.gr/)).

The main goal of my thesis was the improvement of the cohabitation between humans and pets, with the aid of modern technology and its ability to have a positive impact towards that goal, but also with the active participation of users themselves. So, this mobile application was designed and partially developed, according to the principles of [human-centered design](https://en.wikipedia.org/wiki/User-centered_design) and with the user's opinion and experience in mind, on every stage of the process.

![TAVOO screenshots](https://raw.githubusercontent.com/gkffzs/TAVOO/master/app_scrns.png)

#### Version 0.6
This is the [pre-alpha version](https://en.wikipedia.org/wiki/Software_release_life_cycle#Pre-alpha) of the application (numbered 0.6), so there are a lot of things to be done, till it's properly completed. Due to the nature of the whole design and development of the application, I believe that only through collaboration and more active participation of the users is there a future for this application. That is why I decided from the start that it would be published as free software. Moreover, I won't be able to maintain or develop it further for a long period of time, so if anyone is interested in continuing it, they are more than welcome.

#### Structure
As for the structure of the application, please bear in mind that it was the first Android application I ever developed. I made an effort to apply various programming principles, and I also tried various methods and technologies. Therefore, I'm sure there are a lot that can be improved. Whatever the case, here is a brief description of the main files in `/app/src/main/java/gr/upatras/ceid/kaffezas/tavoo/`:

<table>
  <tr>
    <td><b>AboutActivity.java</b></td>
  </tr>
  <tr>
    <td><i>Source code of the Activity that's responsible for the display of information regarding the application (i.e. its version, a description, etc.).  </i></td>
  </tr>
  
  <tr>
    <td><b>AppLicenceActivity.java</b></td>
  </tr>
  <tr>
    <td><i>Source code of the Activity that's responsible for displaying the licence under which the application's released.</i></td>
  </tr>
  
  <tr>
    <td><b>AppServices.java</b></td>
  </tr>
  <tr>
    <td><i>Source code of several methods that are used throughout the application. It specifically contains a method for displaying custom Toast messages, for reading files from the device, for logging users, and its respective AsyncTask, that sends the said information to the server.</i></td>
  </tr>
  
  <tr>
    <td><b>AutoCheckOutReceiver.java</b></td>
  </tr>
  <tr>
    <td><i>Source code of a class that manages the auto-check-out process from a location after 3 hours.</i></td>
  </tr>
  
  <tr>
    <td><b>ConnectionManager.java</b></td>
  </tr>
  <tr>
    <td><i>This file is responsible for the communication between the application and the server, and so it includes most of the related methods. More precisely, it includes a method for checking the connection to the internet, a method for sending and receiving data from a location, and a bunch of methods for creating proper URLs that are needed in some functions (such as rating). They are based on HttpURLConnection.</i></td>
  </tr>
  
  <tr>
    <td><b>DirectionsJSONParser.java</b></td>
  </tr>
  <tr>
    <td><i>This is the source code of a parser that reads the data in JSON format that Google's Directions API sends, using a method that decodes the navigational instructions.</i></td>
  </tr>
  
  <tr>
    <td><b>GooglePlayLicenceActivity.java</b></td>
  </tr>
  <tr>
    <td><i>Source code of the Activity that's responsible for displaying the licence under which Google Maps are used in the application.</i></td>
  </tr>
  
  <tr>
    <td><b>LoginActivity.java</b></td>
  </tr>
  <tr>
    <td><i>Source code of the Activity that's responsible for the login screen of the application, and that manages the related data by saving them in SharedPreferences.</i></td>
  </tr>
  
  <tr>
    <td><b>MainActivity.java</b></td>
  </tr>
  <tr>
    <td><i>Source code of the main Activity of the application, namely the screen that appears right after the start of the application and the splash screen. It includes the main menu of the application, with three major functions (profile, play locations and vets), as well as a drop-down menu for login, licenses, etc.</i></td>
  </tr>
  
  <tr>
    <td><b>PlayLocation.java</b></td>
  </tr>
  <tr>
    <td><i>This is the source file of a class that is used for the representation and easier handling of a play location and its related data.</i></td>
  </tr>
  
  <tr>
    <td><b>PlayLocationActivity.java</b></td>
  </tr>
  <tr>
    <td><i>Source code of the Activity that displays the locations available for play and walk on the map. One of the most lengthy files of the application, it includes several methods. As a brief summary, it starts by defining how the locations will be displayed on the map, and then specifies the location of the user. If that's successfully done, it proceeds by finding the nearest location, displays all of them on the map and draws the route towards the nearest. It then updates the information that are displayed on the information box at the bottom of the screen, and specifies the methods that will be "listening" to the actions of the user (i.e. addition of a new location).</i></td>
  </tr>
  
  <tr>
    <td><b>PlayLocationXMLParser.java</b></td>
  </tr>
  <tr>
    <td><i>This source file is a parser that reads the location data in XML format that are received from the server.</i></td>
  </tr>
  
  <tr>
    <td><b>PrivacyPolicyActivity.java</b></td>
  </tr>
  <tr>
    <td><i>Source code of the Activity that displays the privacy policy of the application.</i></td>
  </tr>
  
  <tr>
    <td><b>ProfileActivity.java</b></td>
  </tr>
  <tr>
    <td><i>Source code of the Activity that displays the profile of a dog. It's not developed yet, so only a warning message is displayed.</i></td>
  </tr>
  
  <tr>
    <td><b>RegisterActivity.java</b></td>
  </tr>
  <tr>
    <td><i>Source code of the Activity that is responsible for the registration of a new user. It performs some checks, displays some loading messages and calls the respective methods in order to send data to the server, via AsyncTask.</i></td>
  </tr>
  
  <tr>
    <td><b>SessionManager.java</b></td>
  </tr>
  <tr>
    <td><i>This source file is responsible for the communication between the application and the server, mainly for the functions of login and registration. It is based on HttpComponents by Apache.</i></td>
  </tr>
  
  <tr>
    <td><b>SplashActivity.java</b></td>
  </tr>
  <tr>
    <td><i>Source code of the splash Activity, that displays the logo of the application for 3 seconds while it is loading.</i></td>
  </tr>
  
  <tr>
    <td><b>Vet.java</b></td>
  </tr>
  <tr>
    <td><i>This source file contains a class that is used for representing a vet, so that the handling of the related data becomes easier.</i></td>
  </tr>
  
  <tr>
    <td><b>VetActivity.java</b></td>
  </tr>
  <tr>
    <td><i>Source code of the Activity for the vets. It basically displays two other Activities in tabs.</i></td>
  </tr>
  
  <tr>
    <td><b>VetAdapter.java</b></td>
  </tr>
  <tr>
    <td><i>This source file helps in the creation of the vet list, by defining where and how the data will be displayed in each item of the list.</i></td>
  </tr>
  
  <tr>
    <td><b>VetFirstActivity.java</b></td>
  </tr>
  <tr>
    <td><i>Source code of the Activity that displays the vets on the map. This file is also one of the most lengthy, and it operates in a similar way to the PlayLocationActivity.</i></td>
  </tr>
  
  <tr>
    <td><b>VetJSONParser.java</b></td>
  </tr>
  <tr>
    <td><i>This source file contains a parser that reads the vet data in JSON format from the server.</i></td>
  </tr>
  
  <tr>
    <td><b>VetSecondActivity.java</b></td>
  </tr>
  <tr>
    <td><i>Source code of the Activity that displays a list with the available vets in the area.</i></td>
  </tr>
</table>

#### Instructions
In order for the application to work properly, several things are needed. More specifically:
+ A working setup of [**TAVOO API**](https://github.com/gkffzs/TAVOO-API) on a server. When it's set, change appropriately the localhost URL that is required in `/app/src/main/java/gr/upatras/ceid/kaffezas/tavoo/ConnectionManager.java`.
+ A Google Maps API key. You can get one by creating an account and following the directions on [Google Developers' site](https://developers.google.com/). When you have one, you should place it in two files, `/app/src/debug/res/values/google_maps_api.xml` and `/app/src/release/res/values/google_maps_api.xml`.

#### Licence
[GNU General Public License version 3](https://www.gnu.org/licenses/gpl-3.0.en.html).
