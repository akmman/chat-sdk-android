package co.chatsdk.ui.custom;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.stfalcon.chatkit.messages.MessageHolders;

import co.chatsdk.core.dao.Keys;
import co.chatsdk.core.dao.Message;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.types.MessageType;
import co.chatsdk.ui.R;
import co.chatsdk.ui.chat.ImageMessageOnClickHandler;
import co.chatsdk.ui.chat.LocationMessageOnClickHandler;
import co.chatsdk.ui.chat.model.ImageMessageHolder;
import co.chatsdk.ui.chat.model.MessageHolder;
import co.chatsdk.ui.view_holders.IncomingImageMessageViewHolder;
import co.chatsdk.ui.view_holders.IncomingTextMessageViewHolder;
import co.chatsdk.ui.view_holders.OutcomingImageMessageViewHolder;
import co.chatsdk.ui.view_holders.OutcomingTextMessageViewHolder;

public class MessageHandler implements IMessageHandler {

    @Override
    public void onBindMessageHolders(Context context, MessageHolders holders, MessageHolders.ContentChecker<MessageHolder> checker) {

        IncomingTextMessageViewHolder.Payload holderPayload = new IncomingTextMessageViewHolder.Payload();
        holderPayload.avatarClickListener = user -> {
            ChatSDK.ui().startProfileActivity(context, user.getEntityID());
        };

        holders.setIncomingTextConfig(IncomingTextMessageViewHolder.class, R.layout.view_holder_incoming_text_message, holderPayload)
        .setOutcomingTextConfig(OutcomingTextMessageViewHolder.class, R.layout.view_holder_outcoming_text_message, holderPayload)
        .setIncomingImageConfig(IncomingImageMessageViewHolder.class, R.layout.view_holder_incoming_image_message, holderPayload)
        .setOutcomingImageConfig(OutcomingImageMessageViewHolder.class, R.layout.view_holder_outcoming_image_message, holderPayload);

    }

    @Override
    public MessageHolder onNewMessageHolder(Message message) {
        if (message.getMessageType().is(MessageType.Image, MessageType.Location)) {
            return new ImageMessageHolder(message);
        }
        return new MessageHolder(message);
    }

    @Override
    public void onClick(Activity activity, View rootView, Message message) {
        if (message.getMessageType().is(MessageType.Image)) {
            ImageMessageOnClickHandler.onClick(activity, rootView, message.stringForKey(Keys.ImageUrl));
        }
        if (message.getMessageType().is(MessageType.Location)) {
            double longitude = message.doubleForKey(Keys.MessageLongitude);
            double latitude = message.doubleForKey(Keys.MessageLatitude);

            LocationMessageOnClickHandler.onClick(activity, new LatLng(latitude, longitude));
        }
    }

    @Override
    public void onLongClick(Activity activity, View rootView, Message message) {

    }

    @Override
    public boolean hasContentFor(MessageHolder message, byte type) {
        return type == MessageType.Text || type == MessageType.Image && message instanceof ImageMessageHolder;
    }
}
