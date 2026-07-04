package com.dt.animations;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.util.Duration;

@SuppressWarnings("deprecation")
public class FadeInLeftTransition extends ConfigAnimation {

    public FadeInLeftTransition(final Node node) {
        super(
                node,
                new Timeline(
                        new KeyFrame(Duration.millis(0),
                                new KeyValue(node.opacityProperty(), 0, WEB_EASE),
                                new KeyValue(node.translateXProperty(), -20, WEB_EASE)
                        ),
                        new KeyFrame(Duration.millis(700),
                                new KeyValue(node.opacityProperty(), 1, WEB_EASE),
                                new KeyValue(node.translateXProperty(), 0, WEB_EASE)
                        )
                )
        );

        setCycleDuration(Duration.seconds(1));
        setDelay(Duration.seconds(0));
    }
}