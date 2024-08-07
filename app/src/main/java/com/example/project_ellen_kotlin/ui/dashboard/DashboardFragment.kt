package com.example.project_ellen_kotlin.ui.dashboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.project_ellen_kotlin.MainActivity
import com.example.project_ellen_kotlin.databinding.FragmentDashboardBinding
import com.example.project_ellen_kotlin.ui.SharedViewModel

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private lateinit var activity: MainActivity
    private lateinit var safeContext: Context

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
        val textView: TextView = binding.textDashboard
        val imageView: ImageView? = null
        val viewModel =
            ViewModelProvider(this).get(SharedViewModel::class.java)
//        viewModel.receiptList.observe(viewLifecycleOwner) {
//            // Perform action with latest data
//            textView.text = "TESTING"
//        }
        viewModel.text.observe(viewLifecycleOwner) {
            textView.text = "TESTING"
        }

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root


//        viewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}