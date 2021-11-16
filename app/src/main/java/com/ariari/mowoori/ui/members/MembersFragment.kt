package com.ariari.mowoori.ui.members

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.ariari.mowoori.R
import com.ariari.mowoori.base.BaseFragment
import com.ariari.mowoori.databinding.FragmentMembersBinding
import com.ariari.mowoori.ui.members.adapter.MembersAdapter
import com.ariari.mowoori.ui.register.entity.User
import com.ariari.mowoori.ui.register.entity.UserInfo
import com.ariari.mowoori.util.toastMessage
import com.ariari.mowoori.widget.InviteDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MembersFragment : BaseFragment<FragmentMembersBinding>(R.layout.fragment_members) {
    private val membersViewModel: MembersViewModel by viewModels()
    private val membersAdapter = MembersAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = membersViewModel
        membersViewModel.fetchGroupInfo()
        setOpenDialogEventObserver()
        setCurrentGroupObserver()
        setMembersListObserver()
        setMembersRvAdapter()
    }

    private fun setOpenDialogEventObserver() {
        membersViewModel.openInviteDialogEvent.observe(viewLifecycleOwner, {
            it.peekContent()?.let { groupId ->
                showInviteDialog(groupId)
            }
        })
    }

    private fun setCurrentGroupObserver(){
        membersViewModel.currentGroup.observe(viewLifecycleOwner){
            membersViewModel.fetchMemberList()
        }
    }

    private fun setMembersListObserver(){
        membersViewModel.membersList.observe(viewLifecycleOwner){
            membersAdapter.submitList(it)
        }
    }

    private fun setMembersRvAdapter() {
        binding.rvMembers.adapter = membersAdapter
    }

    private fun showInviteDialog(groupId: String) {
        InviteDialogFragment(
            groupId, object : InviteDialogFragment.InviteDialogListener {
                override fun onPositiveClick(dialog: DialogFragment) {
                    shareText(groupId)
                    dialog.dismiss()
                }

                override fun onNegativeClick(dialog: DialogFragment) {
                    dialog.dismiss()
                }

                override fun onCopyClick(dialog: DialogFragment, inviteCode: String) {
                    val clipboard: ClipboardManager =
                        requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                    val clip: ClipData = ClipData.newPlainText("MoWoori_Invite_Code", inviteCode)
                    clipboard.setPrimaryClip(clip)

                    requireContext().toastMessage(R.string.members_invite_code_copy_complete)
                }
            }).show(parentFragmentManager, this.javaClass.name)
    }

    private fun shareText(text: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(shareIntent)
    }

}
