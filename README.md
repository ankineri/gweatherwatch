# gweatherwatch

![Watchface image](https://raw.githubusercontent.com/ankineri/gweatherwatch/master/Extras/screen.png)


Garmin Weather Watch

This is a watchface designed to be fully customizable via layout.xml values. You can move around elements, add and remove elements to the watchface.

All drawables are extensible, which means that you can inherit from any drawable and make a new one which suits your taste.

Unlike other watchfaces, the location is fetched from two places:
- The last activity (that's the traditional way)
- From the phone, using the companion app.
This setup theoretically improves the location accuracy, since the watch updates location only when it uses GPS, which is during activities. Phones acquire locations much more frequently.

# Folders
- GarminFW -> The watchface code. Most of the code goes here.
- Google User Script -> The _backend_ of the watchface that fecthes online data.
- Android -> The companion application to fetch the coordinates
- Extras -> stuff that is not exactly needed for the watchface.
