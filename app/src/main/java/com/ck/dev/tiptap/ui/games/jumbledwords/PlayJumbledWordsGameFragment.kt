package com.ck.dev.tiptap.ui.games.jumbledwords

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.extensions.getGameExitPopup
import com.ck.dev.tiptap.extensions.setHeaderBgColor
import com.ck.dev.tiptap.helpers.AppConstants
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

    private lateinit var charAdapter: RandomCharAdapter
    private lateinit var crosswordPuzzleView: CrosswordPuzzleView
    private var size: Int = 0
    private lateinit var words: String
    private lateinit var unJumbledWord: Array<UnJumbledWord>
    private val jumbledFormed: ArrayList<String> = ArrayList()
    private val wordsDone: ArrayList<String> = ArrayList()
    private lateinit var timer: CountDownTimer
    private var timeSpentInEndless: Long = 0
    private var infTime = 9999999999L

    private lateinit var timeLimit: IntArray
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
        size = words.length
        var paddingCell = 0
        if (size == 3 || size == 4) {
            paddingCell = 2
            size += paddingCell
        }
        setRandomChars()
        setListeners()
        val listOfRules: ArrayList<JumbledWordData> = getListOfRules()
        crosswordPuzzleView = CrosswordPuzzleView(requireContext(), listOfRules, size)
        play_jumbled_root.addView(crosswordPuzzleView)
        startTimer(infTime - timeSpentInEndless)
        setBackButtonHandling()
        lifecycleScope.launchWhenCreated {
            val existData =
                viewModel.getHighScore(gameName, level)
            withContext(Dispatchers.Main) {
                if (existData != null) {
                    stars.rating = existData.toFloat()
                } else {
                    stars.rating = 0f
                }
            }
        }
        jumbled_level_tv.text = getString(R.string.current_level, level)
        (requireActivity() as AppCompatActivity).setHeaderBgColor(R.color.primaryDarkColor)
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
                if (wordsDone.size == jumbledFormed.size) {
                    showGameCompletePopup()
                }
            }
        }
    }

    private fun startTimer(gameTimeLimit: Long) {
        Timber.i("startTimer called")
        timer = object : CountDownTimer(gameTimeLimit, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeSpentInEndless = infTime - millisUntilFinished
                jumbled_timer.text =
                    getString(R.string.time_left, (timeSpentInEndless / 1000).toString())
            }

            override fun onFinish() {
                //exitGame()
                Timber.i("timer onFinish called")
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
        gridLayoutManager.flexDirection = FlexDirection. ROW
        gridLayoutManager.flexWrap = FlexWrap.WRAP
        play_jumbled_chars.layoutManager = gridLayoutManager
        play_jumbled_chars.adapter = charAdapter


        /*val gridLayoutManager =
            GridLayoutManager(requireContext(), 5*//*, LinearLayoutManager.HORIZONTAL, false*//*)
        play_jumbled_chars.layoutManager = gridLayoutManager
        play_jumbled_chars.adapter = charAdapter*/
    }

    private fun initGameInputs() {
        Timber.i("initGameInputs called")
        words = gameArgs.word
        unJumbledWord = gameArgs.unjumbledWords
        timeLimit = gameArgs.timeLimits
        level = gameArgs.level
        gameName = gameArgs.gameMode
    }

    private fun getIndexes(word: String): UnJumbledWord? {
        return unJumbledWord.find {
            it.word == word
        }
    }

    private fun getListOfRules(): java.util.ArrayList<JumbledWordData> {
        Timber.i("getListOfRules called")
        val list = Array(size) { i -> Array(size) { j -> JumbledWordData("$i-$j", false, ' ', false) } }
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
        for (i in 0 until size) {
            for (j in 0 until size) {
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
        //charAdapter.notifyItemRemoved()
    }

    private fun setNextLevel() {
        Timber.i("setNextLevel called")
        val rulesJson = (requireActivity() as AppCompatActivity).readJsonFromAsset(
            AppConstants.JUMBLED_WORDS_GAME_RULE_FILE_NAME
        )
        val json = JSONObject(rulesJson)
        val easyGameRules = json.getJSONArray("easy").toString()
        val obj = Gson().fromJson(easyGameRules, Array<JumbledWordGameLevelData>::class.java)
        val gameRule = obj[level.toInt()]
        timer.cancel()
        val action =
            PlayJumbledWordsGameFragmentDirections.actionPlayJumbledWordsGameFragmentSelf(
                word = gameRule.word,
                unjumbledWords = gameRule.unJumbledWords,
                level = gameRule.level,
                timeLimits = gameRule.timeLimit.toIntArray(),
                gameMode = gameName
            )
        /*val action = GameLevelsFragmentDirections.actionGameLevelsFragmentToGameScreenFragment(name)
        v.findNavController().navigate(action)*/
        navController.navigate(action)
    }

    private fun setBackButtonHandling() {
        Timber.i("setBackButtonHandling called")
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            val dialogData = requireContext().getGameExitPopup({
                if (::navController.isInitialized) {
                    exitGame()
                }

            }, {
                startTimer(infTime - timeSpentInEndless)
            })
            timer.cancel()
            val instance = ConfirmationDialog.newInstance(dialogData)
            instance.isCancelable = false
            instance.show(parentFragmentManager, AppConstants.GAME_EXIT_TAG)
        }
    }

    private fun showGameCompletePopup() {
        Timber.i("showGameCompletePopup called")
        val oneStar = 1
        val twoStar = 2
        val threeStar = 3
        val star =
            if (timeSpentInEndless / 1000 < timeLimit[0]) threeStar else if (timeSpentInEndless / 1000 < timeLimit[1]) twoStar else oneStar
        lifecycleScope.launch {
            viewModel.apply {
                updateHighScoreIfApplicable(
                    gameName,
                    level, star
                )
                updateGameLevel(
                    gameName,
                    (level.toInt() + 1).toString()
                )
                updateTotalGamePlayed(gameName)
                updateTotalTimePlayed(gameName, timeSpentInEndless / 1000)
            }
        }
        val dialogData = DialogData(
            title = getString(R.string.jumbled_game_complete_title),
            content = getString(
                R.string.jumbled_game_complete_content,
                star.toString(),
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
        timer.cancel()
        val instance = ConfirmationDialog.newInstance(dialogData)
        instance.isCancelable = false
        instance.show(parentFragmentManager, AppConstants.GAME_COMPLETE_TAG)
    }

    private fun exitGame(it: DialogFragment? = null) {
        Timber.i("exitGame called")
        navController.navigate(R.id.action_playJumbledWordsGameFragment_to_jumbledWordsMenuScreenFragment)
        it?.dismiss()
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