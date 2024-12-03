package com.example.project_ellen_kotlin.ui.email

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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
    private lateinit var textView: TextView
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



        textView = binding.emailMessage
        viewModel.emailMessage.observe(viewLifecycleOwner) {
            textView.text = it
        }

        binding.emailSendBtn.setOnClickListener{sendEmail()}

        return root
    }

    private fun sendEmail() {
        if (!(binding.emailMessage.text.toString().isEmpty()
                    || binding.emailSubject.text.toString().isEmpty()
                    || binding.emailRecipient.text.toString().isEmpty())) {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(binding.emailRecipient.text.toString()))
            intent.putExtra(Intent.EXTRA_SUBJECT, binding.emailSubject.text.toString())
            intent.putExtra(Intent.EXTRA_TEXT, binding.emailMessage.text.toString())
            intent.putExtra(Intent.EXTRA_STREAM, viewModel.listOfReceipts[0].getUri())
            intent.setData(Uri.parse("mailto:"))
            if (intent.resolveActivity(safeContext.packageManager) != null) {
                startActivity(intent)
            } else {
                val dialog: AlertDialog.Builder = AlertDialog.Builder(safeContext)
                dialog.setTitle("There is no application on your device that supports sending emails.")
                dialog.setPositiveButton("OK") { it, _ -> it.dismiss() }
                dialog.show()
            }
        } else {
            val dialog: AlertDialog.Builder = AlertDialog.Builder(safeContext)
            dialog.setTitle("Please fill in all the fields.")
            dialog.setPositiveButton("OK") { it, _ -> it.dismiss() }
            dialog.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}