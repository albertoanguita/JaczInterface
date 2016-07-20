To run vdio, extract the downloaded file to any location of your choice and execute the launch.bat file.


LGPL libraries
--------------

Video makes use of LGPL licenced software. See the instructions below to replace the provided library with one of your own.

 - Weupnp:
  --------
  Weupnp is used to open ports on routers through the plug and play interface (https://bitletorg.github.io/weupnp/).
  Currently, version 0.1.4 of this library is used by vdio.
  To replace this library with another version, remove the file weupnp-0.1.4.jar located ad ./lib/thirdparty/weupnp
  and paste in that same directory the new library version.