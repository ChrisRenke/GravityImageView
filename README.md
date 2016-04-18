GravityImageView
===========================

A small simple-to-use ImageView subclass that supports gravity for image placement as well as a mix of easy-to-understand scaling types. Great for hero images or times when you need an image to scale-n-crop as well as be anchored somewhere besides merely the center.

![Gravtiy Image View Demo](https://github.com/chrisrenke/gravityimageview/raw/master/giv.gif)


## Usage

Using the view in xml is super easy. Just point at the desired src like you would with a standard ImageView and specify the `imageGravity` like you with `gravity` on any other view. Additionally, `imageScaleMode` will let you tweak how the image is 1:1 scaled into the view.

```xml
<com.chrisrenke.giv.GravityImageView
    android:id="@+id/hero_image"
    android:layout_width="match_parent"
    android:layout_height="140dp"
    android:src="@drawable/dinosaur_2000"
    app:imageGravity="center_horizontal|bottom"
    app:imageScaleMode="crop"
    />
```
The `imageGravity` or `imageScaleMode` can also be changed programmatically.

```java
gravityImageView.setImageGravity(RIGHT|CENTER_VERTICAL);
…
gravityImageView.setImageScaleMode(INSIDE);
```


## Gradle

Just add this line to your build.gradle:

```groovy
compile 'com.chrisrenke.giv:giv:1.0'
```


## License

    Copyright 2016 Chris Renke

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
