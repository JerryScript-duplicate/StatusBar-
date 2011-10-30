StatusBar+ - A custom, open source status bar for ANY Android device.
=============

Copyright (C) 2011 Thomas James Barrasso

Author: Thomas James Barrasso  
Name: StatusBar+  
Version: Alpha Seven  
License: Apache License, Version 2.0  

Description:
-------

StatusBar+ is designed to allow any and all Android users access to a custom status bar without the need to root their device or install a custom ROM. It is fairly simple: a background service is run, an View is added to the defauly Window using WindowManager and given TYPE_SYSTEM_ALERT. This is the only TYPE_SYSTEM_* Window allowed by applications that are not build using the platform signature. Of course a special permission, android.permission.SYSTEM_ALERT_WINDOW, is required to do so. Essentially every other permission is required to monitor the system's state including battery, WiFi, phone signal, etc.

How to build:
-------

You CANNOT build this application in its current state because it relies on the WP7UI Android Library Project that is owned by Thomas James Barrasso. This library is not open source. This may leave you asking, why open source the application then? The answer is simple: to allow other developers to use the same method to create their own custom status bars, perhaps even a themable one. You will notice that this application has very few assets and that is because in the WP7UI Library ALL icons are drawn in code using the Canvas. This is to make inclusion of the library simplier (no resources means no dependencies/ struggles) and allows for the most accurate information (the battery can be from 0 - 100 without 100 different images!

License:
-------

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Support:
-------

Do you appreciate this application? Consider purchasing the paid version on the Android Market http://market.android.com/details?id=com.tombarrasso.android.wp7bar, or donating via PayPal. To do so see http://tombarrasso.com/. Anything is welcome! If you would like to give complements or need assistance feel free to email me at contact@tombarrasso.com, I will do my best to respond!
