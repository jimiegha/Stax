package com.hover.stax.paybill

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.hover.stax.R
import com.hover.stax.databinding.DialogPaybillNumberBinding
import com.hover.stax.utils.NavUtil
import com.hover.stax.views.AbstractStatefulInput
import com.hover.stax.views.StaxDialog
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class PaybillNumberDialog : DialogFragment() {

    private var _binding: DialogPaybillNumberBinding? = null
    private val binding get() = _binding!!

    private lateinit var dialog: StaxDialog
    private lateinit var dialogView: View

    private val viewModel: PaybillViewModel by sharedViewModel()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogPaybillNumberBinding.inflate(layoutInflater)
        dialog = StaxDialog(requireActivity(), binding.root).makeSticky().setDialogTitle(R.string.business_number_prompt).setNegButton(R.string.btn_cancel) { dismiss() }

        dialogView = dialog.mView

        return dialog.createIt()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = dialogView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.doneBtn.setOnClickListener {
            viewModel.setBusinessNumber(binding.businessNoInput.text)
            if (validates())
               findNavController().popBackStack()
        }
    }

    private fun validates(): Boolean {
        val businessNoError = viewModel.businessNoError()
        binding.businessNoInput.setState(businessNoError, if (businessNoError == null) AbstractStatefulInput.SUCCESS else AbstractStatefulInput.ERROR)

        return businessNoError == null
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

}