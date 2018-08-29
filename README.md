<h1 align="center">
  <br>
  <img width=50% src="https://github.com/MedAnisBenSalah/Slicky-Futon/blob/master/docs/Logo.png">
</h1>

<h4 align="center">
  Android game using the <a href="https://libgdx.badlogicgames.com/">LibGDX</a> engine.
</h4>

<p align="center">
  <a href="#introduction">Introduction</a> •
  <a href="#components">Components</a> •
  <a href="#extensions">File extensions</a> •
  <a href="#media">Media</a> •
  <a href="#links">Links</a>
</p>

# Introduction

As of 2016, i began working on an Android game called "The Fish Game", the idea was to have flappy bird's mechanics, 
friendly graphics and an eye catching environement, but somewhere into the development process of the game, i realised
that i lacked the necessary knowledge/resources to make beautiful graphics assets, so i quickly shifted the game into a
simpler, less textured and more shape oriented graphics.

As of September 2017, the game was officially released on Google's Play Store under the developer's name "OrMisicL Apps",
and amassed more than 2000 downloads worldwide, but ended up getting taken down along with my suspended developer's account
sometime in March 2018.

So here is it, fully open source for anyone to re-release it, improve it or simply learn from it.

# Components

<p align="center">
  <a href="#generator">Generator</a> •
  <a href="#encoder">Encoder</a> •
  <a href="#android">Android</a> •
  <a href="#core">Core</a>
</p>

## Generator

<p align="center">
<img src="https://github.com/MedAnisBenSalah/Slicky-Futon/blob/master/docs/Generator.png">
</p>

This will convert any <a href="https://github.com/MovingBlocks/box2d-editor">LibGDX's physics body editor tool</a> JSON file 
into a ready-to-use <a href="#obm">OBM</a> file, simply pick a directory containing all of your JSON files and hit the "Generate"
button, the output files will be generated under "(Your picked directory)/Generated", refer to the <a href="https://github.com/MedAnisBenSalah/Slicky-Futon/tree/master/generator/assets">
assets folder</a> for a better example.

## Encoder

<p align="center">
<img src="https://github.com/MedAnisBenSalah/Slicky-Futon/blob/master/docs/Encoder.png">
</p>

This will encode configuration files using Base64, and will generate an <a href="#oac">OAC</a> file, simply pick a valid XML file and hit the "Encode"
button, the output file will be generated under your picked directory, refer to the <a href="https://github.com/MedAnisBenSalah/Slicky-Futon/tree/master/encoder/assets">
assets folder</a> for a better example.

## Android

Contains a full implementation of <a href="http://appodeal.com/">Appodeal's LibGDX Library</a> (banner, interstitial and rewarded video), as well as a full implementation of Google Play Games Service (<a href="https://github.com/MedAnisBenSalah/Slicky-Futon/tree/master/BaseGameUtils">BaseGameUtils</a>).

This project should not be edited and could be directly imported and used for future implementations of this sort.

## Core

The game's core implements both generic and specific systems, where everything starts from the <a href="https://github.com/MedAnisBenSalah/Slicky-Futon/blob/master/core/src/com/ormisiclapps/slickyfuton/core/Core.java">Core</a> and reports back to it, the Core then delegates tasks to generic <a href="https://github.com/MedAnisBenSalah/Slicky-Futon/tree/master/core/src/com/ormisiclapps/slickyfuton/managers">managers</a> which handle other specific components of the game.

### <a href="https://github.com/MedAnisBenSalah/Slicky-Futon/blob/master/core/src/com/ormisiclapps/slickyfuton/managers/ResourcesManager.java">Resources Manager</a>

Responsible of handling <a href="https://github.com/MedAnisBenSalah/Slicky-Futon/tree/master/android/assets">game's resources</a>, immediate loading, disposing and queued loading, textures quality are also handled internally as well as loading configuration files.

NOTE: This cannot be directly exported and used somewhere else as it relies heavily on the game's <a href="https://github.com/MedAnisBenSalah/Slicky-Futon/blob/master/core/src/com/ormisiclapps/slickyfuton/core/Core.java">Core</a> and some of its components, unless you remove this dependency (which shouldn't be hard), but you'll give away texture's quality handling and configuration's loading.

### <a href="https://github.com/MedAnisBenSalah/Slicky-Futon/blob/master/core/src/com/ormisiclapps/slickyfuton/managers/ModelManager.java">Model Manager</a>

Responsible of handling models loading (<a href="https://github.com/MedAnisBenSalah/Slicky-Futon/blob/master/decoded-assets/Models/Chainsaw/Settings.xml">configuration</a>, <a href="#oab-ormisicl-apps-body-file">body</a>, <a href="https://github.com/MedAnisBenSalah/Slicky-Futon/blob/master/decoded-assets/Models/Chainsaw/Movement-1.xml">movement</a>), and will internally store all loaded models along with their movements and shapes. 

NOTE: This only depends on the File Manager and can be probably used anywhere else.

### <a href="https://github.com/MedAnisBenSalah/Slicky-Futon/blob/master/core/src/com/ormisiclapps/slickyfuton/managers/FileManager.java">File Manager</a>

Responsible of handling game's files, from XML parsing to decoding <a href="#extensions">file extensions</a>, this is basically the lowest layer that the game refers to for handling all of its resources.

NOTE: This only depends on LibGDX's library, so it can be exported and used anywhere outside of this project.

### <a href="https://github.com/MedAnisBenSalah/Slicky-Futon/blob/master/core/src/com/ormisiclapps/slickyfuton/managers/ScreensManager.java">Screens Manager</a>

Will handle and process every <a href="https://github.com/MedAnisBenSalah/Slicky-Futon/tree/master/core/src/com/ormisiclapps/slickyfuton/game/screens">game screen</a> and transition between them, will also handle the <a href="https://github.com/MedAnisBenSalah/Slicky-Futon/tree/master/core/src/com/ormisiclapps/slickyfuton/graphics/windows">UI windows</a> and delegating input between the main UI components and windows.

### <a href="https://github.com/MedAnisBenSalah/Slicky-Futon/blob/master/core/src/com/ormisiclapps/slickyfuton/managers/GraphicsManager.java">Graphics Manager</a>

Is the one responsible for what appears on the screen, will handle rendering cycles (begin, render and finish) and some rendering effects like flashing and fading. 
This is the lowest layer of rendering, meaning anything appearing on the screen has to be passed to this manager and it will be handled accordingly.

### <a href="https://github.com/MedAnisBenSalah/Slicky-Futon/blob/master/core/src/com/ormisiclapps/slickyfuton/managers/LevelManager.java">Level Manager</a>

This is pretty much straight forward, it will load levels from their configuration files and store them internally. 

### Other components

The core's components are well organized and well commented that it makes them easy to understand and re-use, i made sure to write a clean, reusable and well documented code while keeping close to the game's needs, if you have any more questions about the game's core, please contact me by email or leave an issue on this repository and i'll be happy to help. 

# Extensions

<p align="center">
  <a href="#oab-ormisicl-apps-body-file">OAB</a> •
  <a href="#oac-ormisicl-apps-configuration-file">OAC</a> •
  <a href="#oae-ormisicl-apps-effect-file">OAE</a> •
  <a href="#oap-ormisicl-apps-persistence-file">OAP</a>
</p>

## Why

The game would mostly load its own file extensions and formats, these files serve as a dynamic configuration tools to modifying the game
without changing the code (Example).

Most of these files are Base64 encrypted, this would offer an extra layer of security for the final product and could potentially
lower the risks of undesired modifications, other extensions are simply a collection of vertices and points and are constructed in order
to minimalize the stored data size and optimize the loading time.

## OAB (OrMisicL Apps Body) file

This file is generated using the <a href="#generator">Generator tool</a>, and it holds data about the object body's vertices and 
collision, please refer to the <a href="https://github.com/MedAnisBenSalah/Slicky-Futon/blob/master/core/src/com/ormisiclapps/slickyfuton/game/nodes/entity/EntityBodyPartNode.java">
Entity body part node</a> for more insights.

## OAC (OrMisicL Apps Configuration) file

This file is generated using the <a href="#generator">Encoder tool</a>, and it holds data about the object's configuration, please note that this extension
doesn't follow any general guidance as every configuration file follows its own set of rules.
* <a href="https://github.com/MedAnisBenSalah/Slicky-Futon/blob/master/decoded-assets/Models/Chainsaw/Settings.xml">Model configuration example</a>
* <a href="https://github.com/MedAnisBenSalah/Slicky-Futon/blob/master/decoded-assets/Models/Chainsaw/Movement-1.xml">Movement configuration example</a>
* <a href="https://github.com/MedAnisBenSalah/Slicky-Futon/blob/master/decoded-assets/Models/ModelsSettings.xml">Model settings configuration</a>
* <a href="https://github.com/MedAnisBenSalah/Slicky-Futon/blob/master/decoded-assets/Models/PreSetCombinations.xml">PreSet combinations configuration</a>

## OAE (OrMisicL Apps Effect) file

This file is generated using the <a href="https://github.com/libgdx/libgdx/wiki/2D-Particle-Editor">LibGDX's Particle Editor tool</a>, 
and it holds data about particles, please note that this file is a simple extension rename and does not modify the final output of the particle editor.

NOTE: It was originally going to be an encrypted effects file but the idea was dropped as it would be just a waste of resources.

## OAP (OrMisicL Apps Persistence) file

This file is used to save the user's data, it's a Base64 encypted XML based file with basic security machanics,
please refer to How To Use Persistence section for more information.

# Media

<p align=center><b>Gameplay Video</b></p>

[![Gameplay video](http://i3.ytimg.com/vi/_i-6mJCZUzw/maxresdefault.jpg)](https://www.youtube.com/watch?v=_i-6mJCZUzw)

<p align=center><b>First Level Video</b></p>

[![Gameplay video](http://i3.ytimg.com/vi/_i-6mJCZUzw/maxresdefault.jpg)](https://www.youtube.com/watch?v=xPOMeBkGLNY)

# Links

* <a href="https://libgdx.badlogicgames.com/">LibGDX</a> as game engine
* <a href="https://box2d.org/">Box2D</a> as physics engine
* <a href="http://appodeal.com">Appodeal</a> as ad mediation platform
* <a href="https://github.com/grantland/android-samples/tree/master/BaseGameUtils">BaseGameUtils</a> as Google Play games implementation
