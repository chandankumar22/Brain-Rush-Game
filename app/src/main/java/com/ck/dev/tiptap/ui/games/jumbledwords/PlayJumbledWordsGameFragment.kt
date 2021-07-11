package com.ck.dev.tiptap.ui.games.jumbledwords

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.extensions.changeStatusBarColor
import com.ck.dev.tiptap.extensions.getGameExitPopup
import com.ck.dev.tiptap.extensions.handleGameAllLevelComplete
import com.ck.dev.tiptap.extensions.setHeaderBgColor
import com.ck.dev.tiptap.helpers.AppConstants
import com.ck.dev.tiptap.helpers.GameConstants.ENDLESS
import com.ck.dev.tiptap.helpers.GameConstants.JUMBLED_NUMBER_GAME_NAME_ENDLESS
import com.ck.dev.tiptap.helpers.GameConstants.JUMBLED_NUMBER_GAME_NAME_TIME_BOUND
import com.ck.dev.tiptap.helpers.GameConstants.TIME_BOUND
import com.ck.dev.tiptap.helpers.readJsonFromAsset
import com.ck.dev.tiptap.models.*
import com.ck.dev.tiptap.ui.custom.CrosswordPuzzleView
import com.ck.dev.tiptap.ui.dialogs.ConfirmationDialog
import com.ck.dev.tiptap.ui.games.BaseFragment
import com.google.android.flexbox.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_jumbled_words.*
import kotlinx.android.synthetic.main.fragment_find_the_num_game_play_screen.*
import kotlinx.android.synthetic.main.fragment_play_jumbled_words_game.*
import kotlinx.android.synthetic.main.fragment_play_jumbled_words_game.view.*
import kotlinx.android.synthetic.main.list_item_char.view.*
import kotlinx.android.synthetic.main.list_item_number.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber

class PlayJumbledWordsGameFragment : BaseFragment(R.layout.fragment_play_jumbled_words_game) {


    private lateinit var _gameCompletePopup: ConfirmationDialog
    private var isGameCompletePopupToShow = false
    private lateinit var charAdapter: RandomCharAdapter
    private lateinit var crosswordPuzzleView: CrosswordPuzzleView
    private var rowSize: Int = 0
    private var colSize: Int = 0
    private lateinit var words: String
    private lateinit var unJumbledWord: Array<UnJumbledWord>
    private val jumbledFormed: ArrayList<String> = ArrayList()
    private val wordsDone: ArrayList<String> = ArrayList()
    private lateinit var timer: CountDownTimer
    private var timeSpentInEndless: Long = 0
    private var currentTimeSpentInGame: Long = 0
    private var infTime = 9999999999L

    private var timeLimit: Int = 0
    private lateinit var level: String
    private lateinit var gameName: String

    private val gameArgs: PlayJumbledWordsGameFragmentArgs by navArgs()
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.i("onViewCreated called")
        super.onViewCreated(view, savedInstanceState)
        navController =
                Navigation.findNavController(requireActivity(), R.id.jumbled_words_nav_host_fragment)
        initGameInputs()
        var paddingCell = 0
        if (rowSize == 3 || rowSize == 4) {
            paddingCell = 2
            rowSize += paddingCell
        }
        if (colSize == 3 || colSize == 4) {
            paddingCell = 2
            colSize += paddingCell
        }
        setRandomChars()
        setListeners()
        val listOfRules: ArrayList<JumbledWordData> = getListOfRules()
        crosswordPuzzleView = CrosswordPuzzleView(requireContext(), listOfRules, rowSize, colSize)
        play_jumbled_root.addView(crosswordPuzzleView)
        if (gameName == JUMBLED_NUMBER_GAME_NAME_ENDLESS) startTimer(infTime - timeSpentInEndless)
        else startTimer((timeLimit * 1000).toLong())
        setBackButtonHandling()
        lifecycleScope.launchWhenCreated {
            val existData =
                    viewModel.getHighScore(gameName, level)
            withContext(Dispatchers.Main) {
                if (existData != null) {
                    best_score.text = existData.toString()
                } else {
                    best_score.text = 0.toString()
                }
            }
        }
        jumbled_level_tv.text = getString(R.string.current_level, level)
        (requireActivity() as AppCompatActivity).setHeaderBgColor(R.color.primaryDarkColor)
        requireActivity().findViewById<ConstraintLayout>(R.id.header).visibility = View.GONE
        (requireActivity() as AppCompatActivity).changeStatusBarColor(R.color.primaryDarkColor)
    }

    private fun setListeners() {
        cancel.setOnClickListener {
            if (inp_text.text.trim().isNotEmpty()) {
                val existing = inp_text.text.toString()
                val new = existing.replace(existing[inp_text.text.toString().length - 1], ' ')
                inp_text.text = new.trim()
                charAdapter.insertChar(existing[existing.length - 1])
            }
        }
        submit.setOnClickListener {
            val word = inp_text.text.toString().trim()
            if (word.isNotEmpty()) {
                var isWord = false
                if (wordsDone.contains(word)) {
                    Toast.makeText(requireContext(), "$word is already found", Toast.LENGTH_SHORT)
                            .show()
                } else {
                    getIndexes(word)?.let {
                        if (::crosswordPuzzleView.isInitialized) {
                            isWord = true
                            crosswordPuzzleView.revealTheChar(it.startPos, it.endPos)
                            setRandomChars()
                            inp_text.text = ""
                        }
                    }
                    if (isWord) wordsDone.add(word)
                    else Toast.makeText(requireContext(), "$word is not a word", Toast.LENGTH_SHORT)
                            .show()
                    if (wordsDone.size == jumbledFormed.size || crosswordPuzzleView.isAllCharRevealed()) {
                        showGameCompletePopup()
                    }
                }
            }
        }
    }

    private fun initGameInputs() {
        Timber.i("initGameInputs called")
        words = gameArgs.word
        unJumbledWord = gameArgs.unjumbledWords
        timeLimit = gameArgs.timeLimit
        level = gameArgs.level
        gameName = gameArgs.gameMode
        rowSize = gameArgs.rowSize
        colSize = gameArgs.colSize
    }

    private fun startTimer(gameTimeLimit: Long) {
        Timber.i("startTimer called")
        timer = object : CountDownTimer(gameTimeLimit, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (gameName == JUMBLED_NUMBER_GAME_NAME_ENDLESS) {
                    timeSpentInEndless = infTime - millisUntilFinished
                    jumbled_timer.text =
                            getString(R.string.time_left, (timeSpentInEndless / 1000).toString())
                } else {
                    currentTimeSpentInGame = millisUntilFinished
                    jumbled_timer.text =
                            getString(R.string.time_left, (millisUntilFinished / 1000).toString())
                }
            }

            override fun onFinish() {
                Timber.i("timer onFinish called")
                if (gameName == JUMBLED_NUMBER_GAME_NAME_TIME_BOUND) {
                    showGameCompletePopup()
                }
            }
        }
        timer.start()
    }

    private fun setRandomChars() {
        Timber.i("setRandomChars called")
        val chars = words.toCharArray().toMutableList() as ArrayList
        chars.shuffle()
        charAdapter = RandomCharAdapter(chars, this)
        val gridLayoutManager = FlexboxLayoutManager(requireContext())
        gridLayoutManager.justifyContent = JustifyContent.CENTER
        gridLayoutManager.alignItems = AlignItems.CENTER
        gridLayoutManager.flexDirection = FlexDirection.ROW
        gridLayoutManager.flexWrap = FlexWrap.WRAP
        play_jumbled_chars.layoutManager = gridLayoutManager
        play_jumbled_chars.adapter = charAdapter


        /*val gridLayoutManager =
            GridLayoutManager(requireContext(), 5*//*, LinearLayoutManager.HORIZONTAL, false*//*)
        play_jumbled_chars.layoutManager = gridLayoutManager
        play_jumbled_chars.adapter = charAdapter*/
    }

    private fun getIndexes(word: String): UnJumbledWord? {
        return unJumbledWord.find {
            it.word == word
        }
    }

    private fun getListOfRules(): java.util.ArrayList<JumbledWordData> {
        Timber.i("getListOfRules called")
        val list = Array(rowSize) { i -> Array(colSize) { j -> JumbledWordData("$i-$j", false, ' ', false) } }
        var charPos = 0
        unJumbledWord.forEach {
            jumbledFormed.add(it.word)
            var startPosI: Int
            var startPosJ: Int
            var endPosI: Int
            var endPosJ: Int
            it.startPos.split("-").apply {
                startPosI = this[0].toInt()
                startPosJ = this[1].toInt()
            }
            it.endPos.split("-").apply {
                endPosI = this[0].toInt()
                endPosJ = this[1].toInt()
            }
            if (startPosI != endPosI && startPosJ == endPosJ) {//0-1 to 2-1 (vertical)
                for (i in startPosI..endPosI) {
                    list[i][endPosJ] = JumbledWordData("$i-$endPosJ", true, it.word[charPos], false)
                    charPos++
                }
            } else if (startPosJ != endPosJ && startPosI == endPosI) {//1-0 to 1-2 (horizontal)
                for (i in startPosJ..endPosJ) {
                    list[startPosI][i] = JumbledWordData("$startPosI-$i", true, it.word[charPos], false)
                    charPos++
                }
            }
            charPos = 0
        }
        val ret = ArrayList<JumbledWordData>()
        for (i in 0 until rowSize) {
            for (j in 0 until colSize) {
                ret.add(list[i][j])
            }
        }
        Timber.i("ret: ${ret.toList()}")

        return ret

        /*val list = ArrayList<JumbledWordData>()

        for(i in 0 until size){
            list.add(JumbledWordData("1-1", true, 'A', true))
        }
        return list*/
    }

    fun letterClicked(letter: String) {
        Timber.i("letterClicked clicked")
        val text = inp_text.text
        inp_text.text = "$text$letter"
    }

    private fun setNextLevel() {
        Timber.i("setNextLevel called")
        val rulesJson = (requireActivity() as AppCompatActivity).readJsonFromAsset(
                AppConstants.JUMBLED_WORDS_GAME_RULE_FILE_NAME
        )
        val json = JSONObject(rulesJson)
        val easyGameRules = if (gameName == JUMBLED_NUMBER_GAME_NAME_ENDLESS) json.getJSONArray(ENDLESS).toString() else if (gameName == JUMBLED_NUMBER_GAME_NAME_TIME_BOUND) json.getJSONArray(TIME_BOUND).toString() else json.getJSONArray("hard").toString()
        val obj = Gson().fromJson(easyGameRules, Array<JumbledWordGameLevelData>::class.java)
        timer.cancel()
        if (level.toInt() == obj.size) {
            requireContext().handleGameAllLevelComplete()
            exitGame()
        } else {
            val gameRule = obj[level.toInt()]
            val action =
                    PlayJumbledWordsGameFragmentDirections.actionPlayJumbledWordsGameFragmentSelf(
                            word = gameRule.word,
                            unjumbledWords = gameRule.unJumbledWords,
                            level = gameRule.level,
                            timeLimit = gameRule.timeLimit,
                            gameMode = gameName,
                            rowSize = gameRule.rowSize,
                            colSize = gameRule.colSize
                    )
            navController.navigate(action)
        }
    }

    private fun setBackButtonHandling() {
        Timber.i("setBackButtonHandling called")
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            val dialogData = requireContext().getGameExitPopup({
                if (::navController.isInitialized) {
                    exitGame()
                }

            }, {
                if (gameName == JUMBLED_NUMBER_GAME_NAME_ENDLESS) startTimer(infTime - timeSpentInEndless)
                else startTimer(currentTimeSpentInGame)
            })
            timer.cancel()
            val instance = ConfirmationDialog.newInstance(dialogData)
            instance.isCancelable = false
            instance.show(parentFragmentManager, AppConstants.GAME_EXIT_TAG)
        }
    }

    private fun showGameCompletePopup() {
        Timber.i("showGameCompletePopup called")
        lifecycleScope.launch {
            viewModel.apply {
                if (gameName == JUMBLED_NUMBER_GAME_NAME_ENDLESS) updateHighScoreIfApplicable(
                        gameName,
                        level, (timeSpentInEndless / 1000).toInt(), isLowScoreToSave = true
                ) else {
                    updateHighScoreIfApplicable(
                            gameName,
                            level, wordsDone.size
                    )
                }
                updateGameLevel(
                        gameName,
                        (level.toInt() + 1).toString()
                )
                updateTotalGamePlayed(gameName)
                updateTotalTimePlayed(gameName, timeSpentInEndless / 1000)
            }
        }
        val dialogData = if (gameName == JUMBLED_NUMBER_GAME_NAME_ENDLESS) {
            DialogData(
                    title = getString(R.string.jumbled_game_complete_title),
                    content = getString(
                            R.string.jumbled_game_endless_complete_content,
                            (timeSpentInEndless / 1000).toString()
                    ),
                    posBtnText = getString(R.string.game_complete_positive_btn_txt),
                    negBtnText = getString(R.string.game_complete_negative_btn_txt),
                    posListener = {
                        if (::navController.isInitialized) {
                            navController.navigate(R.id.action_playJumbledWordsGameFragment_self)
                            setNextLevel()
                        }
                    },
                    megListener = {
                        exitGame()
                    }
            )
        } else {
            DialogData(
                    title = getString(R.string.game_complete_title),
                    content = getString(
                            R.string.jumbled_game_time_bound_complete_content,
                            wordsDone.size.toString(),
                            currentTimeSpentInGame.toString()
                    ),
                    posBtnText = getString(R.string.game_complete_positive_btn_txt),
                    negBtnText = getString(R.string.game_complete_negative_btn_txt),
                    posListener = {
                        if (::navController.isInitialized) {
                            navController.navigate(R.id.action_playJumbledWordsGameFragment_self)
                            setNextLevel()
                        }
                    },
                    megListener = {
                        exitGame()
                    }
            )
        }
        timer.cancel()
        _gameCompletePopup = ConfirmationDialog.newInstance(dialogData)
        _gameCompletePopup.isCancelable = false
        if (lifecycle.currentState == Lifecycle.State.RESUMED) {
            _gameCompletePopup.show(parentFragmentManager, AppConstants.GAME_COMPLETE_TAG)
        } else {
            isGameCompletePopupToShow = true
        }
    }

    private fun exitGame(it: DialogFragment? = null) {
        Timber.i("exitGame called")
        it?.dismiss()
        val mode = when (gameName) {
            JUMBLED_NUMBER_GAME_NAME_ENDLESS -> ENDLESS
            JUMBLED_NUMBER_GAME_NAME_TIME_BOUND -> TIME_BOUND
            else -> ""
        }
        val action = PlayJumbledWordsGameFragmentDirections.actionPlayJumbledWordsGameFragmentToJumbledWordsLevelsFragment(mode)
        navController.navigate(action)
    }

    override fun onResume() {
        super.onResume()
        if (isGameCompletePopupToShow) {
            isGameCompletePopupToShow = false
            _gameCompletePopup.show(parentFragmentManager, AppConstants.GAME_COMPLETE_TAG)
        }
    }

}

class RandomCharAdapter(val list: ArrayList<Char>, private val ctx: PlayJumbledWordsGameFragment) :
        RecyclerView.Adapter<RandomCharAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_char, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.apply {
            char_tv.text = list[position].toString()
            setOnClickListener {
                ctx.letterClicked(char_tv.text.toString())
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, itemCount - position);
                list.remove(list[position])
            }
        }
    }

    fun insertChar(char: Char) {
        list.add(char)
        notifyItemInserted(itemCount)
    }

    override fun getItemCount() = list.size

}