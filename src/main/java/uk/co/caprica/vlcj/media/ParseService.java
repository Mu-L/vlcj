/*
 * This file is part of VLCJ.
 *
 * VLCJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VLCJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VLCJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2009-2019 Caprica Software Limited.
 */

package uk.co.caprica.vlcj.media;

import uk.co.caprica.vlcj.enums.ParseFlag;
import uk.co.caprica.vlcj.enums.MediaParsedStatus;

/**
 * Media parsing functionality.
 * <p>
 * Parsing is always asynchronous - you must wait for a parsed changed event before you can read meta data.
 * <p>
 * Once a media has been parsed, it can not be parsed again - so if for example parse fails due to a timeout and you
 * retry it, it will not parse again.
 * <p>
 * There are two special timeout values that can be used for parsing:
 * <ul>
 *     <li>0 means that the call will wait indefinitely;</li>
 *     <li>-1 means that the default value set by the "preparse-timeout" native option/argument will be used</li>
 * </ul>
 * Parsing may trigger the remote downloading of data, including for example performing a search for cover art for the
 * media. This has clear privacy implications.
 * <p>
 * To affirmatively prevent all network access for meta data, consider trying the "--no-metadata-network-access"
 * argument when creating a {@link uk.co.caprica.vlcj.factory.MediaPlayerFactory}.
 * <p>
 * It should also be possible to prevent such network accesses by using appropriate {@link ParseFlag} values, see
 * {@link ParseFlag#FETCH_LOCAL} vs {@link ParseFlag#FETCH_NETWORK}.
 * <p>
 * Even with network access disabled, some media covert art may still appear locally (e.g. ~/.cache/vlc) - this does not
 * necessarily mean that a remote network request was made for the cover art, rather the art was embedded in the media
 * file and extracted to this temporary cache directory.
 */
public class ParseService extends BaseService {

    ParseService(Media media) {
        super(media);
    }

    public boolean parse() {
        return parse(-1, (ParseFlag[]) null);
    }

    public boolean parse(int timeout) {
        return parse(timeout, (ParseFlag[]) null);
    }

    public boolean parse(ParseFlag... flags) {
        return parse(-1, flags);
    }

    public boolean parse(int timeout, ParseFlag... flags) {
        return libvlc.libvlc_media_parse_with_options(mediaInstance, flagsToInt(flags), timeout) == 0;
    }

    public void stop() {
        libvlc.libvlc_media_parse_stop(mediaInstance);
    }

    public MediaParsedStatus status() {
        return MediaParsedStatus.mediaParsedStatus(libvlc.libvlc_media_get_parsed_status(mediaInstance));
    }

    private int flagsToInt(ParseFlag... flags) {
        int result = 0;
        if (flags != null) {
            for (ParseFlag flag : flags) {
                result |= flag.intValue();
            }
        }
        return result;
    }

}