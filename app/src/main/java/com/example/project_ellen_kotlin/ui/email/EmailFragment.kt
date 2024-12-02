package com.example.project_ellen_kotlin.ui.email

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.project_ellen_kotlin.MainActivity
import com.example.project_ellen_kotlin.R
import com.example.project_ellen_kotlin.Receipt
import com.example.project_ellen_kotlin.databinding.FragmentEmailBinding
import com.example.project_ellen_kotlin.ui.SharedViewModel

class EmailFragment : Fragment() {

    private var _binding: FragmentEmailBinding? = null
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recipient = view.findViewById<EditText>(R.id.email_recipient)
        val subject = view.findViewById<EditText>(R.id.email_subject)
        val message = view.findViewById<EditText>(R.id.email_message)
        val sendBtn = view.findViewById<Button>(R.id.email_send_btn)


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEmailBinding.inflate(inflater, container, false)
        val root: View = binding.root



//        val textView: TextView = binding.textEmail
//        viewModel.emailMessage.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
//
//        viewModel.emailMessage.observe(viewLifecycleOwner) {email ->
//            if (email != null) {
//                sendEmail(email)
//            }
//        }

        return root
    }

    private fun sendEmail(recipient : String, subject : String, message : String, receipt: Receipt) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}