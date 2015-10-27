## TAVOO *(to be updated)*
This repository contains the files for [**TAVOO**](https://play.google.com/store/apps/details?id=gr.upatras.ceid.kaffezas.tavoo), a [free software](https://www.gnu.org/philosophy/free-sw.html) Android application that I partially developed as part of my diploma thesis (as an undergraduate student of the [Computer Engineering & Informatics Department](https://www.ceid.upatras.gr/) at the [University of Patras](http://www.upatras.gr/)).

The main goal of my thesis was the improvement of the cohabitation between humans and pets, with the aid of modern technology and its ability to have a positive impact towards that goal, but also with the active participation of users themselves. So, this mobile application was designed and partially developed, according to the principles of [human-centered design](https://en.wikipedia.org/wiki/User-centered_design) and with the user's opinion and experience in mind, on every stage of the process.

#### Version 0.6
This is the [pre-alpha version](https://en.wikipedia.org/wiki/Software_release_life_cycle#Pre-alpha) of the application (numbered 0.6), so there are a lot of things to be done, till it's properly completed. Due to the nature of the whole design and development of the application, I believe that only through collaboration and more active participation of the users is there a future for this application. That is why I decided from the start that it would be published as free software. Moreover, I won't be able to maintain or develop it further for a long period of time, so if anyone is interested in continuing it, they are more than welcome.

#### Structure
As for the structure of the application, please bear in mind that it was the first Android application I ever developed. I made an effort to apply various programming principles, and I also tried various methods and technologies. Therefore, I'm sure there are a lot that can be improved. Whatever the case, here is a brief description of the main files in `/app/src/main/java/gr/upatras/ceid/kaffezas/tavoo/`:

<table>
  <tr>
    <td><b>AboutActivity.java</b></td>
  </tr>
  <tr>
    <td><i></i></td>
  </tr>
  
  <tr>
    <td><b>AppLicenceActivity.java</b></td>
  </tr>
  <tr>
    <td><i></i></td>
  </tr>
  
  <tr>
    <td><b>AppServices.java</b></td>
  </tr>
  <tr>
    <td><i></i></td>
  </tr>
  
  <tr>
    <td><b>AutoCheckOutReceiver.java</b></td>
  </tr>
  <tr>
    <td><i></i></td>
  </tr>
  
  <tr>
    <td><b>ConnectionManager.java</b></td>
  </tr>
  <tr>
    <td><i></i></td>
  </tr>
  
  <tr>
    <td><b>DirectionsJSONParser.java</b></td>
  </tr>
  <tr>
    <td><i></i></td>
  </tr>
  
  <tr>
    <td><b>GooglePlayLicenceActivity.java</b></td>
  </tr>
  <tr>
    <td><i></i></td>
  </tr>
  
  <tr>
    <td><b>LoginActivity.java</b></td>
  </tr>
  <tr>
    <td><i></i></td>
  </tr>
  
  <tr>
    <td><b>MainActivity.java</b></td>
  </tr>
  <tr>
    <td><i></i></td>
  </tr>
  
  <tr>
    <td><b>PlayLocation.java</b></td>
  </tr>
  <tr>
    <td><i></i></td>
  </tr>
  
  <tr>
    <td><b>PlayLocationActivity.java</b></td>
  </tr>
  <tr>
    <td><i></i></td>
  </tr>
  
  <tr>
    <td><b>PlayLocationXMLParser.java</b></td>
  </tr>
  <tr>
    <td><i></i></td>
  </tr>
  
  <tr>
    <td><b>PrivacyPolicyActivity.java</b></td>
  </tr>
  <tr>
    <td><i></i></td>
  </tr>
  
  <tr>
    <td><b>ProfileActivity.java</b></td>
  </tr>
  <tr>
    <td><i></i></td>
  </tr>
  
  <tr>
    <td><b>RegisterActivity.java</b></td>
  </tr>
  <tr>
    <td><i></i></td>
  </tr>
  
  <tr>
    <td><b>SessionManager.java</b></td>
  </tr>
  <tr>
    <td><i></i></td>
  </tr>
  
  <tr>
    <td><b>SplashActivity.java</b></td>
  </tr>
  <tr>
    <td><i></i></td>
  </tr>
  
  <tr>
    <td><b>Vet.java</b></td>
  </tr>
  <tr>
    <td><i></i></td>
  </tr>
  
  <tr>
    <td><b>VetActivity.java</b></td>
  </tr>
  <tr>
    <td><i></i></td>
  </tr>
  
  <tr>
    <td><b>VetAdapter.java</b></td>
  </tr>
  <tr>
    <td><i></i></td>
  </tr>
  
  <tr>
    <td><b>VetFirstActivity.java</b></td>
  </tr>
  <tr>
    <td><i></i></td>
  </tr>
  
  <tr>
    <td><b>VetJSONParser.java</b></td>
  </tr>
  <tr>
    <td><i></i></td>
  </tr>
  
  <tr>
    <td><b>VetSecondActivity.java</b></td>
  </tr>
  <tr>
    <td><i></i></td>
  </tr>
</table>

#### Instructions
In order for the application to work properly, several things are needed. More specifically:
+ A working setup of [**TAVOO API**](https://github.com/gkffzs/TAVOO-API) on a server. When it's set, change appropriately the localhost URL that is required in `/app/src/main/java/gr/upatras/ceid/kaffezas/tavoo/ConnectionManager.java`.
+ A Google Maps API key. You can get one by creating an account and following the directions on [Google Developers' site](https://developers.google.com/). When you have one, you should place it in two files, `/app/src/debug/res/values/google_maps_api.xml` and `/app/src/release/res/values/google_maps_api.xml`.

#### Licence
[GNU General Public License version 3](https://www.gnu.org/licenses/gpl-3.0.en.html).
