package sdk.chat.custom_message.audio;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.messages.MessageHolders;

import co.chatsdk.android.app.R;
import co.chatsdk.core.dao.Keys;
import co.chatsdk.core.dao.Message;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.types.MessageType;
import co.chatsdk.ui.chat.model.MessageHolder;
import co.chatsdk.ui.custom.Customiser;
import co.chatsdk.ui.custom.IMessageHandler;
import sdk.chat.custom_message.sticker.StickerMessageHolder;
import sdk.chat.custom_message.video.IncomingVideoMessageViewHolder;
import sdk.chat.custom_message.video.OutcomingVideoMessageViewHolder;
import sdk.chat.custom_message.video.PlayVideoActivity;
import sdk.chat.custom_message.video.VideoMessageHolder;

/**
 * Created by ben on 9/28/17.
 */

public class AudioMessageModule  {
    public static void activate() {
        ChatSDK.a().audioMessage = new BaseAudioMessageHandler();

        Customiser.shared().addMessageHandler(new IMessageHandler() {
            @Override
            public boolean hasContentFor(MessageHolder message, byte type) {
                return type == MessageType.Audio && message instanceof AudioMessageHolder;
            }

            @Override
            public void onBindMessageHolders(Context ctx, MessageHolders holders, MessageHolders.ContentChecker<MessageHolder> checker) {
                holders.registerContentType(
                        (byte) MessageType.Audio,
                        IncomingAudioMessageViewHolder.class,
                        R.layout.view_holder_incoming_audio_message,
                        OutcomingAudioMessageViewHolder.class,
                        R.layout.view_holder_outcoming_audio_message, checker);
            }

            @Override
            public MessageHolder onNewMessageHolder(Message message) {
                if (message.getMessageType().is(MessageType.Audio)) {
                    return new AudioMessageHolder(message);
                }
                return null;
            }

            @Override
            public void onClick(Activity activity, View rootView, Message message) {
                String videoURL = (String) message.valueForKey(Keys.MessageVideoURL);
                if(videoURL != null) {
                    Intent intent = new Intent(activity, PlayVideoActivity.class);
                    intent.putExtra(Keys.IntentKeyFilePath, videoURL);
                    activity.startActivity(intent);
                }
            }

            @Override
            public void onLongClick(Activity activity, View rootView, Message message) {

            }
        });


    }
}
