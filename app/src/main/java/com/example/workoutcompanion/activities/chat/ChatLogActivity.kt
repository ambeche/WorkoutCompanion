package com.example.workoutcompanion.activities.chat

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.workoutcompanion.R
import com.example.workoutcompanion.activities.home.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.imageview_chat_to_row
import kotlinx.android.synthetic.main.image_from_chat.view.*
import kotlinx.android.synthetic.main.image_to_row.view.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.concurrent.schedule

class ChatLogActivity : AppCompatActivity() {

    val adapter = GroupAdapter<ViewHolder>()

    var toUser: User? = null

    var selectedPhoto: Uri?= null

    var sharableimg:Bitmap? = null

    var imagetobeshared :String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        //supportActionBar?.title = "Chat Log"

        recyclerview_chat_log.layoutManager = LinearLayoutManager(this)
        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = toUser?.username



        recyclerview_chat_log.adapter = adapter



            listenForMessages()
           listenIMAGEMessages()




        send_button_chat_log.setOnClickListener {

            Log.d("SendMessage","Attempt to send Message")

            performSendMessage()


        }
        upload_Button.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type ="image/*"
            startActivityForResult(intent,12)
            edittext_chat_log.isEnabled = false
        }

    }



    private fun performSendMessage() {
        // how do we actually send a message to firebase...
        val text = edittext_chat_log.text.toString()

        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user.uid

        if (fromId == null) return

        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/${fromId}/${toId}").push()

        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis() / 1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d("SendMessage", "Saved our chat message: ${reference.key}")
                edittext_chat_log.text.clear()
                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
            }
        toReference.setValue(chatMessage)

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(chatMessage)

        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)

    }



    private fun uploadImageToFirebasestorage() {

        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user.uid



        ///
        if(selectedPhoto == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/${filename}")


        ref.putFile(selectedPhoto!!).addOnSuccessListener {
            Log.d("Register","Succefuly iploaded image ${it.metadata?.path}")
            //imagetobeshared = it.metadata?.path

            ref.downloadUrl.addOnSuccessListener {
                //it.toString()
                Log.d("register","File location :${it.toString()}")
                imagetobeshared = it.toString()
               // saveUserrodatabse(it.toString())

            }

        }.addOnFailureListener {
            Log.d("Register","Failed to select photo")
        }

        Timer("SettingUp", false).schedule(3000) {

            val reference = FirebaseDatabase.getInstance().getReference("/Image-user-messages/${fromId}/${toId}").push()

            val toReference = FirebaseDatabase.getInstance().getReference("/Image-user-messages/${toId}/${fromId}").push()

            val ImagechatMessage = ImagChatMessage(reference.key!!, imagetobeshared!!, fromId!!, toId!!, System.currentTimeMillis() / 1000)
            reference.setValue(ImagechatMessage)
                .addOnSuccessListener {
                    Log.d("SendImgMessage", "Saved our chat Imag message: ${reference.key}")
                    // edittext_chat_log.text.clear()
                    recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
                }
            toReference.setValue(ImagechatMessage)


        }


    }


    private fun listenIMAGEMessages() {



           val fromId = FirebaseAuth.getInstance().uid
           val toId = toUser?.uid
           val ref = FirebaseDatabase.getInstance().getReference("/Image-user-messages/${fromId}/${toId}")

           ref.addChildEventListener(object: ChildEventListener {

               override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                   val chatImageMessage = p0.getValue(ImagChatMessage::class.java)

                   if (chatImageMessage != null) {
                       Log.d("ListenToImageMessage",chatImageMessage.bitm)

                       if (chatImageMessage.fromId == FirebaseAuth.getInstance().uid) {
                           val currentUser = MainActivity.currentUser ?: return
                           adapter.add(ChatFromImageItem(chatImageMessage.bitm,currentUser))
                       } else {
                           adapter.add(ChatToImageItem(chatImageMessage.bitm,toUser!!))
                       }
                   }else {
                       Log.d("ListenImage","${chatImageMessage?.bitm}")
                   }

               }

               override fun onCancelled(p0: DatabaseError) {

               }

               override fun onChildChanged(p0: DataSnapshot, p1: String?) {

               }

               override fun onChildMoved(p0: DataSnapshot, p1: String?) {

               }

               override fun onChildRemoved(p0: DataSnapshot) {

               }

           })




    }
    private fun listenForMessages() {

        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/${fromId}/${toId}")

        ref.addChildEventListener(object: ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)

                if (chatMessage != null) {
                    Log.d("ListenToMessage", chatMessage.text)

                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        val currentUser = MainActivity.currentUser ?: return
                        adapter.add(ChatFromItem(chatMessage.text,currentUser))
                    } else {
                        adapter.add(ChatToItem(chatMessage.text,toUser!!))
                    }
                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })

    }


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 12 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("UploadImagshare", "pic was selected")

            selectedPhoto = data.data


            val uri_final = getCapturedImage(selectedPhoto)

            sharableimg = uri_final

            Log.d("Share1","Final_URi :   ${sharableimg}")
            Log.d("Share2","The URL to string  ${sharableimg.toString()}")
            // val BITMAP = BitmapDrawable(uri_final)
            //  button_Img.setBackgroundDrawable(BITMAP)
            //selectPhotoImageview.setImageBitmap(uri_final)

            performImgSharing()


        }
    }

    private fun performImgSharing() {

      //  val text = edittext_chat_log.text.toString()

        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user.uid


        if (fromId == null || sharableimg == null) return

        uploadImageToFirebasestorage()

        //val reference = FirebaseDatabase.getInstance().getReference("/Image-user-messages/${fromId}/${toId}").push()

       // val toReference = FirebaseDatabase.getInstance().getReference("/Image-user-messages/$toId/$fromId").push()

        //val ImagechatMessage = ImagChatMessage(reference.key!!, sharableimg.toString(), fromId, toId, System.currentTimeMillis() / 1000)
       /*   reference.setValue(ImagechatMessage)
            .addOnSuccessListener {
                Log.d("SendImgMessage", "Saved our chat Imag message: ${reference.key}")
               // edittext_chat_log.text.clear()
                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
            }
        toReference.setValue(ImagechatMessage)*/

       // val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        //latestMessageRef.setValue(ImagechatMessage)

       // val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
       // latestMessageToRef.setValue(ImagechatMessage)

    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun getCapturedImage(selectedPhotoUri: Uri?): Bitmap? {

        var uri1:Bitmap? = null
        val bitmap = when {
            Build.VERSION.SDK_INT <= 28 ->
                MediaStore.Images.Media.getBitmap(
                    this.contentResolver,
                    selectedPhotoUri
                )
            else -> {
                val source = ImageDecoder.createSource(this.contentResolver, selectedPhotoUri!!)
                uri1 =  ImageDecoder.decodeBitmap(source)
            }
        }
        return uri1

    }


}

class ChatFromItem(val message:String,val user:User): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_to_row.text = message

        val uri = user.profileImag
        val targetImageView = viewHolder.itemView.imageview_chat_to_row
        Picasso.get().load(uri).into(targetImageView)

    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}

////For Image
class ChatFromImageItem(val messageUrl:String?,val user:User): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        Log.d("insideChatFrom","received:  ${messageUrl}")

        val imagetoshow = viewHolder.itemView.imageSent_To_row
        Picasso.get().load(messageUrl).into(imagetoshow)
       // viewHolder.itemView.imageview_chat_to_row.setImageBitmap(image)

        val uri = user.profileImag
        val targetImageView = viewHolder.itemView.imageview_chat_to_row
        Picasso.get().load(uri).into(targetImageView)

    }

    override fun getLayout(): Int {
        return R.layout.image_to_row
    }
}

class ChatToImageItem(val messageUrl:String?,val user:User): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {

        val imagetoshow = viewHolder.itemView.imageSent_from_row


        Picasso.get().load(messageUrl).into(imagetoshow)

       // viewHolder.itemView.imageview_chat_from_row.setImageBitmap(image)

        val uri = user.profileImag
        val targetImageView = viewHolder.itemView.imageview_chat_to_row
        Picasso.get().load(uri).into(targetImageView)

    }

    override fun getLayout(): Int {
        return R.layout.image_from_chat
    }
}



class ChatToItem(val message:String,val user:User): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_from_row.text = message

        val uri = user.profileImag
        val targetImageView = viewHolder.itemView.imageview_chat_from_row
        Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}