package com.ck.dev.tiptap.ui.games.findthenumber

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.adapters.FindTheNumGridAdapter
import com.ck.dev.tiptap.adapters.GridNumSelectCallback
import com.ck.dev.tiptap.data.entity.Games
import com.ck.dev.tiptap.extensions.fetchDrawable
import com.ck.dev.tiptap.extensions.getRandomColor
import com.ck.dev.tiptap.helpers.AppConstants
import com.ck.dev.tiptap.helpers.AppConstants.FIND_THE_NUM_GAME_RULE_FILE_NAME
import com.ck.dev.tiptap.helpers.AppConstants.GAME_COMPLETE_TAG
import com.ck.dev.tiptap.helpers.AppConstants.GAME_EXIT_TAG
import com.ck.dev.tiptap.helpers.AppConstants.GAME_PAUSE_TAG
import com.ck.dev.tiptap.helpers.GameConstants.FIND_THE_NUMBER_GAME_NAME
import com.ck.dev.tiptap.helpers.readJsonFromAsset
import com.ck.dev.tiptap.models.DialogData
import com.ck.dev.tiptap.models.GameLevelData
import com.ck.dev.tiptap.models.GridNumberElement
import com.ck.dev.tiptap.models.VisibleNumberElement
import com.ck.dev.tiptap.ui.GameLevelsFragmentDirections
import com.ck.dev.tiptap.ui.custom.MarqueeLayout
import com.ck.dev.tiptap.ui.dialogs.ConfirmationDialog
import com.ck.dev.tiptap.ui.games.BaseFragment
import com.ck.dev.tiptap.viewmodelfactories.FindTheNumberVmFactory
import kotlinx.android.synthetic.main.fragment_find_the_num_game_play_screen.*
import kotlinx.android.synthetic.main.list_item_visible_number.view.*
import kotlinx.coroutines.launch
import timber.log.Timber

class PlayGameScreenFragment : BaseFragment(R.layout.fragment_find_the_num_game_play_screen),
    GridNumSelectCallback {

    private var isPaused: Boolean = false
    private lateinit var timer: CountDownTimer
    private lateinit var navController: NavController
    private lateinit var m: MarqueeLayout
    private lateinit var gridAdapter: FindTheNumGridAdapter
    private var gridSize: Int = 0
    private val replFactor: Int = 10
    private var visibleNumListSize: Int = 0
    private var listOfNums = arrayListOf<VisibleNumberElement>()
    private var gridNums = arrayListOf<GridNumberElement>()
    private var currentScore = 0
    private var endLimit = 0
    private var gameTimeLimit: Long = 2000
    private var currentTimeSpentInGame: Long = 0
    private var isPausePopUpShown = false
    private var isExit = false
    private lateinit var currentLevel: String
    private val viewModel: FindTheNumberViewModel by activityViewModels {
        FindTheNumberVmFactory(requireContext())
    }
    private val gameArgs: PlayGameScreenFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.i("onViewCreated called")
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        gridSize = gameArgs.gridSize
        endLimit = gridSize * gridSize
        visibleNumListSize = gameArgs.visibleNums
        gameTimeLimit = gameArgs.time
        currentLevel = gameArgs.level
        level_tv.text = getString(R.string.current_level, currentLevel)
        setGrid()
        setMovingListNumbers()
        find_the_num_root.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.primaryDarkColor
            )
        )
        startTimer(gameTimeLimit)
        live_score_tv.text = currentScore.toString()
        reload.setOnClickListener {
            setMovingListNumbers()
        }
        setBackButtonHandling()
        handlePlayPauseGame()
    }

    private fun handlePlayPauseGame() {
        Timber.i("handlePlayPauseGame called")
        pause_play_block.setOnClickListener {
            Timber.i("pause_play_block.setOnClickListener called")
            if (!isPaused) {
                pause_play_block.apply {
                    text = "Resume"
                    setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        requireContext().fetchDrawable(R.drawable.ic_resume_game),
                        null,
                        null
                    )
                }
                timer.cancel()
                currentTimeSpentInGame
            } else {
                pause_play_block.apply {
                    text = "Pause"
                    setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        requireContext().fetchDrawable(R.drawable.ic_pause_game),
                        null,
                        null
                    )
                }
                startTimer(currentTimeSpentInGame)
            }
            isPaused = !isPaused
        }
    }

    private fun setBackButtonHandling() {
        Timber.i("setBackButtonHandling called")
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            val dialogData = DialogData(
                title = getString(R.string.game_exit_title),
                content = getString(R.string.game_exit_content),
                posBtnText = getString(R.string.game_exit_positive_btn_txt),
                negBtnText = getString(R.string.game_exit_negative_btn_txt),
                posListener = {
                    if (::navController.isInitialized) {
                        isExit = true
                        exitGame()
                    }

                },
                megListener = {
                    startTimer(currentTimeSpentInGame)
                }
            )
            timer.cancel()
            val instance = ConfirmationDialog.newInstance(dialogData)
            instance.isCancelable = false
            instance.show(parentFragmentManager, GAME_EXIT_TAG)
        }
    }

    private fun startTimer(gameTimeLimit: Long) {
        Timber.i("startTimer called")
        timer = object : CountDownTimer(gameTimeLimit, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (!isExit) {
                    currentTimeSpentInGame = millisUntilFinished
                    timer_tv.text =
                        getString(R.string.time_left, (millisUntilFinished / 1000).toString())
                }
            }

            override fun onFinish() {
                //exitGame()
                showGameCompletePopup()
            }
        }
        timer.start()
    }

    override fun onPause() {
        Timber.i("onPause called")
        super.onPause()
        if (!isExit) {
            isPausePopUpShown = true
            timer.cancel()
        }
    }

    private fun setGrid() {
        Timber.i("setGrid called")
        for (i in 0 until endLimit) {
            for (j in 0 until replFactor) {
                gridNums.add(GridNumberElement((i + 1), getRandomColor()))
            }
        }
        val list = (gridNums.shuffled() as ArrayList<GridNumberElement>).subList(0, endLimit)
        gridNums = ArrayList(list)
        gridAdapter = FindTheNumGridAdapter(gridNums, this)
        val gridLayoutManager =
            GridLayoutManager(requireContext(), gridSize/*, LinearLayoutManager.HORIZONTAL, false*/)
        grid_num_list.layoutManager = gridLayoutManager
        /// StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        grid_num_list.adapter = gridAdapter

    }

    override fun onResume() {
        Timber.i("onResume called isPausePopUpShown: $isPausePopUpShown")
        super.onResume()
        /*for (listOfNum in listOfNums) {
            createTextView("${listOfNum.number}")
        }*/
        if (isPausePopUpShown) {
            showGamePausePopup()
        }
    }

    override fun numSelected(number: Int) {
        if (number == -1) {
            currentScore--
        } else {
            currentScore++
            val currentGrid = gridAdapter.getCurrentGrid()
            val positionIdentified = listOfNums.indexOfFirst {
                it.number == number
            }
            val element = listOfNums[positionIdentified]
            if (element.occurrences >= 2) {
                element.occurrences--
                for (i in 0 until moving_num_list.childCount) {
                    val view = moving_num_list.getChildAt(i)
                    if (view.number_tv.text == number.toString()) {
                        //  view.number_tv.text = newElement.number.toString()
                        view.occurrences_tv.text = element.occurrences.toString()
                    }
                }
            } else {

                listOfNums.remove(element)
                var isNumAlreadyExists: Boolean
                var newElement: GridNumberElement =
                    currentGrid[java.util.Random().nextInt(currentGrid.size - 1)]
                isNumAlreadyExists = listOfNums.indexOfFirst {
                    it.number == newElement.number
                } != -1
                while (isNumAlreadyExists) {
                    newElement = currentGrid[java.util.Random().nextInt(currentGrid.size - 1)]
                    isNumAlreadyExists = listOfNums.indexOfFirst {
                        it.number == newElement.number
                    } != -1
                }
                val count = currentGrid.count {
                    it.number.toString() == newElement.number.toString()
                }
                listOfNums.add(VisibleNumberElement(newElement.number, count))
                for (i in 0 until moving_num_list.childCount) {
                    val view = moving_num_list.getChildAt(i)
                    if (view.number_tv.text == number.toString()) {
                        view.number_tv.text = newElement.number.toString()
                        view.occurrences_tv.text = count.toString()
                    }
                }
                gridAdapter.currentVisibleList(listOfNums)
            }
        }
        live_score_tv.text = currentScore.toString()
    }

    private fun createTextView(element: VisibleNumberElement) {
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.list_item_visible_number, moving_num_list, false)
        view.id = View.generateViewId()
        view.background = requireContext().fetchDrawable(R.drawable.layer_list_number)
        val set = ConstraintSet()
        val totalViews = moving_num_list.childCount
        if (totalViews == 0) {
            moving_num_list.addView(view)
            set.clone(moving_num_list)
            set.connect(view.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            set.connect(view.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
            set.applyTo(moving_num_list)
        } else {
            val prevView = moving_num_list.getChildAt(totalViews - 1)
            moving_num_list.addView(view)
            set.clone(moving_num_list)
            set.connect(prevView.id, ConstraintSet.END, view.id, ConstraintSet.START)
            set.connect(view.id, ConstraintSet.START, prevView.id, ConstraintSet.END)
            set.connect(view.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
            set.applyTo(moving_num_list)
        }
        view.number_tv.text = element.number.toString()
        view.occurrences_tv.text = element.occurrences.toString()

        /* val size = resources.getDimension(R.dimen._36sdp).toInt()
         val param = if (moving_num_list.childCount > 0) LinearLayout.LayoutParams(
             size,
             size
         ) else LinearLayout.LayoutParams(0, size)
         param.weight = if(moving_num_list.childCount>0) 1f / moving_num_list.childCount else 1f
         view.layoutParams = param
         view.number_tv.text = element.number.toString()
         view.occurrences_tv.text = element.occurrences.toString()
         moving_num_list.addView(view)*/
        /*val textView = MaterialTextView(requireContext())
        val size = resources.getDimension(R.dimen._36sdp).toInt()
        val mgn = resources.getDimension(R.dimen._12sdp).toInt()
        val textSize = resources.getDimension(R.dimen.app_text_size_small)
        val param = LinearLayout.LayoutParams(size, size)
        param.setMargins(mgn, mgn, mgn, mgn)
        textView.gravity = Gravity.CENTER
        textView.text = num
        textView.layoutParams = param
        textView.background = requireContext().fetchDrawable(R.drawable.layer_list_button_ui)
        textView.textSize = textSize*/
        /*   val m = MarqueeLayout(requireContext())
           m.setDuration(2000)
           m.addView(textView)
           m.startAnimation()
           moving_num_list.addView(m)*/
    }

    private fun setMovingListNumbers() {
        Timber.i("setMovingListNumbers called")
        listOfNums.clear()
        moving_num_list.removeAllViews()
        var nums = ArrayList<String>()
        for (i in 0 until endLimit) {
            nums.add((i + 1).toString())
        }
        nums = nums.shuffled() as ArrayList<String>
        nums.forEach { num ->
            val count = gridNums.count {
                it.number.toString() == num
            }
            if (count > 0) {
                val it = VisibleNumberElement(num.toInt(), count)
                listOfNums.add(it)
                createTextView(it)
                if (listOfNums.size == visibleNumListSize) {
                    gridAdapter.currentVisibleList(listOfNums)
                    return
                }
            }
        }
        /* listOfNums.forEach {
             if(it.occurrences>0 && i < visibleNumListSize){
                 createTextView(it)
                 i++
             }
             if(i==visibleNumListSize) return@forEach
         }*/
        /*val linearLayoutManager = object  : LinearLayoutManager(requireContext()){
            override fun canScrollHorizontally(): Boolean {
                return false
            }
        }
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        moving_num_list.layoutManager = linearLayoutManager
        moving_num_list.setHasFixedSize(false)
        moving_num_list.isLayoutFrozen = true
        val adapter = MovingListNumbersAdapter(listOfNums)
        moving_num_list.adapter = adapter*/
        /*  val first = linearLayoutManager.findFirstVisibleItemPosition()
          val last = linearLayoutManager.findLastVisibleItemPosition()
          listOfVisibleNums = listOfNums.subList(first, last)
          gridAdapter.currentVisibleList(listOfVisibleNums)*/
        /*   val duration = 10
           val pixelsToMove = 30
           val mHandler = Handler(Looper.getMainLooper())
           val SCROLLING_RUNNABLE: Runnable = object : Runnable {
               override fun run() {
                   moving_num_list.smoothScrollBy(pixelsToMove, 0)
                   mHandler.postDelayed(this, duration.toLong())
               }
           }

           moving_num_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
               override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                   super.onScrolled(recyclerView, dx, dy)
                   val lastItem: Int = linearLayoutManager.findLastCompletelyVisibleItemPosition()
                   val first = linearLayoutManager.findFirstVisibleItemPosition()
                   val last = linearLayoutManager.findLastVisibleItemPosition()
                   // Log.i(localClassName,"before sublist visible list: $listOfVisibleNums")
                   // Log.i(localClassName,"before sublist original list: $listOfNums")
                   listOfVisibleNums = listOfNums.subList(first, last)
                   //Log.i(localClassName,"after sublist visible list: $listOfVisibleNums")
                   //Log.i(localClassName,"after sublist original list: $listOfNums")
                   gridAdapter.currentVisibleList(listOfVisibleNums)
                   if (lastItem == linearLayoutManager.itemCount - 1) {
                       listOfNums.add(lastItem,listOfNums[java.util.Random().nextInt(listOfNums.size)])
                       adapter.notifyItemInserted(lastItem)
                       listOfVisibleNums = listOfNums.subList(first, lastItem)
                       mHandler.removeCallbacks(SCROLLING_RUNNABLE)
                       val postHandler = Handler(Looper.myLooper()!!)
                       postHandler.postDelayed({
                           moving_num_list.adapter = null
                           moving_num_list.adapter = adapter
                           mHandler.postDelayed(SCROLLING_RUNNABLE, 0)
                       }, 0)
                   }
               }
           })
           mHandler.postDelayed(SCROLLING_RUNNABLE, 0)*/
    }

    private fun showGamePausePopup() {
        Timber.i("showGamePausePopup called")
        val dialogData = DialogData(
            title = getString(R.string.game_pause_title),
            content = getString(R.string.game_pause_content),
            posBtnText = getString(R.string.game_pause_positive_btn_txt),
            negBtnText = getString(R.string.game_pause_negative_btn_txt),
            posListener = {
                isPausePopUpShown = false
                startTimer(currentTimeSpentInGame)

            },
            megListener = {
                exitGame()
                isPausePopUpShown = false
            }
        )
        val instance = ConfirmationDialog.newInstance(dialogData)
        instance.isCancelable = false
        instance.show(parentFragmentManager, GAME_PAUSE_TAG)
    }

    private fun exitGame(it: DialogFragment? = null) {
        Timber.i("exitGame called")
        navController.navigate(R.id.action_gameScreenFragment_to_findNumbersMainScreenFragment)
        it?.dismiss()
    }

    private fun showGameCompletePopup() {
        Timber.i("showGameCompletePopup called")
        lifecycleScope.launch {
           /* viewModel.updateGameScore(
                FIND_THE_NUMBER_GAME_NAME,
                (currentLevel.toInt() + 1).toString(),
                GameLevelData(currentLevel, currentScore)
            )*/
            viewModel.apply {
                updateHighScoreIfApplicable(FIND_THE_NUMBER_GAME_NAME,currentLevel,currentScore)
                updateGameLevel(FIND_THE_NUMBER_GAME_NAME,(currentLevel.toInt() + 1).toString())
            }
        }

        val dialogData = DialogData(
            title = getString(R.string.game_complete_title),
            content = getString(
                R.string.game_complete_content,
                currentScore.toString(),
                (gameTimeLimit / 1000).toString()
            ),
            posBtnText = getString(R.string.game_complete_positive_btn_txt),
            negBtnText = getString(R.string.game_complete_negative_btn_txt),
            posListener = {
                if (::navController.isInitialized) {
                    navController.navigate(R.id.action_gameScreenFragment_self)
                    setNextLevel()
                }
            },
            megListener = {
                exitGame()
                isPausePopUpShown = false
            }
        )
        val instance = ConfirmationDialog.newInstance(dialogData)
        instance.isCancelable = false
        instance.show(parentFragmentManager, GAME_COMPLETE_TAG)
    }

    private fun setNextLevel() {
        Timber.i("setNextLevel called")
        val rulesJson = (requireActivity() as AppCompatActivity).readJsonFromAsset(FIND_THE_NUM_GAME_RULE_FILE_NAME)
        val gameRule = rulesJson[currentLevel.toInt()]
        gameTimeLimit=0L
        timer.cancel()
        val action =
            PlayGameScreenFragmentDirections.actionGameScreenFragmentSelf(
                gridSize = gameRule.gridSize,
                visibleNums = gameRule.visibleNumSize,
                time = gameRule.time, level = gameRule.level
            )
        /*val action = GameLevelsFragmentDirections.actionGameLevelsFragmentToGameScreenFragment(name)
        v.findNavController().navigate(action)*/
        navController.navigate(action)
    }

}