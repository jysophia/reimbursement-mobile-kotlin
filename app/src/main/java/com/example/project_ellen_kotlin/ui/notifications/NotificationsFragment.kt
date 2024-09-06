package com.example.project_ellen_kotlin.ui.notifications

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.project_ellen_kotlin.MainActivity
import com.example.project_ellen_kotlin.databinding.FragmentNotificationsBinding
import com.example.project_ellen_kotlin.ui.SharedViewModel

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private lateinit var activity: MainActivity
    private lateinit var safeContext: Context
    private val viewModel: SharedViewModel by activityViewModels()
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        safeContext = context
        activity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel.imageUri.observe(viewLifecycleOwner) {uri ->
            if (uri != null) {
                sendEmailWithReceipt(uri)
            }
        }

        return root
    }

    private fun sendEmailWithReceipt(uri: Uri) {
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822" // MIME type for email
            putExtra(Intent.EXTRA_SUBJECT, "Subject of the email")
            putExtra(Intent.EXTRA_TEXT, "Body of the email")
            putExtra(Intent.EXTRA_STREAM, uri) // Attach the image URI
            type = "image/*" // MIME type for sending images
        }

        if (emailIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(Intent.createChooser(emailIntent, "Send Email"))
        } else {
            // Handle case where no email app is available
            // You can show a message to the user here
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}