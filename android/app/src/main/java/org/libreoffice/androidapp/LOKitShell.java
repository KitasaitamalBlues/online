/* -*- Mode: Java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*- */
/*
 * This file is part of the LibreOffice project.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.libreoffice;


import android.app.ActivityManager;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.KeyEvent;

import org.libreoffice.canvas.SelectionHandle;
import org.mozilla.gecko.gfx.ComposedTileLayer;

/**
 * Common static LOKit functions, functions to send events.
 */
public class LOKitShell {
    private static final String LOGTAG = LOKitShell.class.getSimpleName();

    public static float getDpi(Context context) {
        if (((LibreOfficeMainActivity)context).isSpreadsheet()) return 96f;
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.density * 160;
    }

    // Get a Handler for the main java thread
    public static Handler getMainHandler() {
        return LibreOfficeApplication.getMainHandler();
    }

    public static void showProgressSpinner(final LibreOfficeMainActivity context) {
        getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                context.showProgressSpinner();
            }
        });
    }

    public static void hideProgressSpinner(final LibreOfficeMainActivity context) {
        getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                context.hideProgressSpinner();
            }
        });
    }

    public static int getMemoryClass(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        return activityManager.getMemoryClass() * 1024 * 1024;
    }

    public static boolean isEditingEnabled() {
        return LibreOfficeMainActivity.isExperimentalMode();
    }

    // EVENTS

    /**
     * Make sure LOKitThread is running and send event to it.
     */
    public static void sendEvent(LOEvent event) {
        LibreOfficeMainActivity.loKitThread.queueEvent(event);
    }

    public static void sendThumbnailEvent(ThumbnailCreator.ThumbnailCreationTask task) {
        LOKitShell.sendEvent(new LOEvent(LOEvent.THUMBNAIL, task));
    }

    /**
     * Send touch event to LOKitThread.
     */
    public static void sendTouchEvent(String touchType, PointF documentTouchCoordinate) {
        LOKitShell.sendEvent(new LOEvent(LOEvent.TOUCH, touchType, documentTouchCoordinate));
    }

    /**
     * Send key event to LOKitThread.
     */
    public static void sendKeyEvent(KeyEvent event) {
        LOKitShell.sendEvent(new LOEvent(LOEvent.KEY_EVENT, event));
    }

    public static void sendSizeChangedEvent(int width, int height) {
        LOKitShell.sendEvent(new LOEvent(LOEvent.SIZE_CHANGED));
    }

    public static void sendSwipeRightEvent() {
        LOKitShell.sendEvent(new LOEvent(LOEvent.SWIPE_RIGHT));
    }

    public static void sendSwipeLeftEvent() {
        LOKitShell.sendEvent(new LOEvent(LOEvent.SWIPE_LEFT));
    }

    public static void sendChangePartEvent(int part) {
        LOKitShell.sendEvent(new LOEvent(LOEvent.CHANGE_PART, part));
    }

    public static void sendLoadEvent(String inputFilePath) {
        LOKitShell.sendEvent(new LOEvent(inputFilePath, LOEvent.LOAD));
    }

    public static void sendNewDocumentLoadEvent(String newDocumentPath, String newDocumentType) {
        LOKitShell.sendEvent(new LOEvent(newDocumentPath, newDocumentType, LOEvent.LOAD_NEW));
    }

    public static void sendSaveAsEvent(String filePath, String fileFormat) {
        LOKitShell.sendEvent(new LOEvent(filePath, fileFormat, LOEvent.SAVE_AS));
    }

    public static void sendResumeEvent(String inputFile, int partIndex) {
        LOKitShell.sendEvent(new LOEvent(LOEvent.RESUME, inputFile, partIndex));
    }

    public static void sendCloseEvent() {
        LOKitShell.sendEvent(new LOEvent(LOEvent.CLOSE));
    }

    /**
     * Send tile reevaluation to LOKitThread.
     */
    public static void sendTileReevaluationRequest(ComposedTileLayer composedTileLayer) {
        LOKitShell.sendEvent(new LOEvent(LOEvent.TILE_REEVALUATION_REQUEST, composedTileLayer));
    }

    /**
     * Send tile invalidation to LOKitThread.
     */
    public static void sendTileInvalidationRequest(RectF rect) {
        LOKitShell.sendEvent(new LOEvent(LOEvent.TILE_INVALIDATION, rect));
    }

    /**
     * Send change handle position event to LOKitThread.
     */
    public static void sendChangeHandlePositionEvent(SelectionHandle.HandleType handleType, PointF documentCoordinate) {
        LOKitShell.sendEvent(new LOEvent(LOEvent.CHANGE_HANDLE_POSITION, handleType, documentCoordinate));
    }

    public static void sendNavigationClickEvent() {
        LOKitShell.sendEvent(new LOEvent(LOEvent.NAVIGATION_CLICK));
    }

    /**
     * Move the viewport to the desired point (top-left), and change the zoom level.
     * Ensure this runs on the UI thread.
     */
    public static void moveViewportTo(final LibreOfficeMainActivity context, final PointF position, final Float zoom) {
        context.getLayerClient().post(new Runnable() {
            @Override
            public void run() {
                context.getLayerClient().moveTo(position, zoom);
            }
        });
    }
}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
