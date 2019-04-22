/*
We have to extend Media from libvlc, because in version 2.1.12 it ignores network-caching option
and we don't want to mess with libraries, rather take libvlc from jcenter.
*/

package com.uniprogy.outquiz.helpers;

import android.net.Uri;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.util.AndroidUtil;
import org.videolan.libvlc.util.HWDecoderUtil;

public class CacheMedia extends Media {

    public CacheMedia(LibVLC libVLC, Uri uri) {
        super(libVLC, uri);
    }

    @Override
    public void setHWDecoderEnabled(boolean enabled, boolean force) {
        HWDecoderUtil.Decoder decoder = enabled ?
                HWDecoderUtil.getDecoderFromDevice() :
                HWDecoderUtil.Decoder.NONE;

        /* Unknown device but the user asked for hardware acceleration */
        if (decoder == HWDecoderUtil.Decoder.UNKNOWN && force)
            decoder = HWDecoderUtil.Decoder.ALL;

        if (decoder == HWDecoderUtil.Decoder.NONE || decoder == HWDecoderUtil.Decoder.UNKNOWN) {
            addOption(":codec=all");
            return;
        }

        /*
         * Set higher caching values if using iomx decoding, since some omx
         * decoders have a very high latency, and if the preroll data isn't
         * enough to make the decoder output a frame, the playback timing gets
         * started too soon, and every decoded frame appears to be too late.
         * On Nexus One, the decoder latency seems to be 25 input packets
         * for 320x170 H.264, a few packets less on higher resolutions.
         * On Nexus S, the decoder latency seems to be about 7 packets.
         */
        String mediaCodecModule = AndroidUtil.isLolliPopOrLater ? "mediacodec_ndk" : "mediacodec_jni";

        final StringBuilder sb = new StringBuilder(":codec=");
        if (decoder == HWDecoderUtil.Decoder.MEDIACODEC || decoder == HWDecoderUtil.Decoder.ALL)
            sb.append(mediaCodecModule).append(",");
        if (force && (decoder == HWDecoderUtil.Decoder.OMX || decoder == HWDecoderUtil.Decoder.ALL))
            sb.append("iomx,");
        sb.append("all");

        addOption(sb.toString());
    }

}
