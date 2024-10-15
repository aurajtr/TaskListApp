package com.example.tracklist

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tracklist.databinding.FragmentTaskListBinding
import com.google.firebase.auth.FirebaseAuth

class TaskListFragment : Fragment() {
    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TaskViewModel by viewModels()
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeTasks()

        binding.addTaskButton.setOnClickListener {
            findNavController().navigate(R.id.action_taskListFragment_to_addTaskFragment)
        }

        binding.filterButton.setOnClickListener { showFilterMenu() }
        binding.sortButton.setOnClickListener { showSortMenu() }
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter { task ->
            val action = TaskListFragmentDirections.actionTaskListFragmentToEditTaskFragment(task.id)
            findNavController().navigate(action)
        }
        binding.taskRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = taskAdapter
        }
    }

    private fun observeTasks() {
        viewModel.filteredTasks.observe(viewLifecycleOwner) { tasks ->
            taskAdapter.submitList(tasks)
        }
    }

    private fun showFilterMenu() {
        val popup = PopupMenu(requireContext(), binding.filterButton)
        popup.menuInflater.inflate(R.menu.menu_filter, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.filter_all -> viewModel.filterTasks(null)
                R.id.filter_work -> viewModel.filterTasks("Work")
                R.id.filter_personal -> viewModel.filterTasks("Personal")
                R.id.filter_shopping -> viewModel.filterTasks("Shopping")
            }
            true
        }
        popup.show()
    }

    private fun showSortMenu() {
        val popup = PopupMenu(requireContext(), binding.sortButton)
        popup.menuInflater.inflate(R.menu.menu_sort, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.sort_date -> viewModel.sortTasks(TaskViewModel.SortOrder.DATE)
                R.id.sort_priority -> viewModel.sortTasks(TaskViewModel.SortOrder.PRIORITY)
                R.id.sort_alphabetical -> viewModel.sortTasks(TaskViewModel.SortOrder.ALPHABETICAL)
            }
            true
        }
        popup.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_task_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                findNavController().navigate(R.id.action_taskListFragment_to_loginFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}