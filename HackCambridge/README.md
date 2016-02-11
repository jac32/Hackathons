<div id="table-of-contents">
<h2>Table of Contents</h2>
<div id="text-table-of-contents">
<ul>
<li><a href="#sec-1">1. App to Web Client Protocol</a>
<ul>
<li><a href="#sec-1-1">1.1. Overview</a></li>
<li><a href="#sec-1-2">1.2. Defined Methods</a></li>
</ul>
</li>
</ul>
</div>
</div>


# App to Web Client Protocol<a id="sec-1" name="sec-1"></a>

## Overview<a id="sec-1-1" name="sec-1-1"></a>

Android application should perform a basic receive -> process -> echo loop

Web client should:
-   Take user input
-   send appropriate commands via socket
-   receive echo response

## Defined Methods<a id="sec-1-2" name="sec-1-2"></a>

The following are the methods for which the server will have a defined behaviour

    forward_move <NUM_MOVEMENTS>
    backward_move <NUM_MOVEMENTS>
    left_turn <ANGLE_OF_MOVEMENT>
    right_turn <ANGLE_OF_MOVEMENT>
