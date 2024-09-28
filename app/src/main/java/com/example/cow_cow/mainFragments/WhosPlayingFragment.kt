class WhosPlayingFragment : Fragment() {

    private lateinit var viewModel: PlayerViewModel
    private lateinit var adapter: PlayerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentWhosPlayingBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_whos_playing, container, false
        )

        // Initialize the ViewModel
        viewModel = ViewModelProvider(requireActivity()).get(PlayerViewModel::class.java)

        // Set up RecyclerView and Adapter
        adapter = PlayerAdapter()
        binding.playerRecyclerView.adapter = adapter
        binding.playerRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Observe changes to the player list
        viewModel.players.observe(viewLifecycleOwner) { players ->
            adapter.submitList(players.toList())
        }

        // Add Player Button Click Listener
        binding.addPlayerButton.setOnClickListener {
            val playerName = binding.playerNameInput.text.toString().trim()
            viewModel.addPlayer(playerName)
            binding.playerNameInput.text.clear()
        }

        // Observe for error messages and show a toast if any
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearErrorMessage() // Clear the error after displaying it
            }
        }

        return binding.root
    }
}
