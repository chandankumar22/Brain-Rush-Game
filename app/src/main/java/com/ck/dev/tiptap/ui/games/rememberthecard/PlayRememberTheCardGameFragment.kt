package com.ck.dev.tiptap.ui.games.rememberthecard

import android.graphics.Bitmap
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.helpers.AppConstants
import com.ck.dev.tiptap.helpers.GameConstants.arrayOfImages
import com.ck.dev.tiptap.helpers.assetToBitmap
import com.ck.dev.tiptap.helpers.getRandomString
import com.ck.dev.tiptap.helpers.readJsonFromAsset
import com.ck.dev.tiptap.models.DialogData
import com.ck.dev.tiptap.models.RememberTheCardData
import com.ck.dev.tiptap.models.RememberTheCardGameRule
import com.ck.dev.tiptap.ui.custom.RememberCardView
import com.ck.dev.tiptap.ui.dialogs.ConfirmationDialog
import com.ck.dev.tiptap.ui.games.BaseFragment
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_play_jumbled_words_game.*
import kotlinx.android.synthetic.main.fragment_play_remember_the_card_game.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import timber.log.Timber
import kotlin.random.Random

class PlayRememberTheCardGameFragment :
    BaseFragment(R.layout.fragment_play_remember_the_card_game) {

    private lateinit var elementToAsk: RememberTheCardData
    private lateinit var gameName: String
    private lateinit var gameMode: String
    private var currentMoves: Int = 0
    private var cardVisibleTime: Int = 0
    private lateinit var rememberCardView: RememberCardView
    private lateinit var navController: NavController
    private lateinit var timer: CountDownTimer
    var isEndless: Boolean = false
    private var isExit: Boolean = false
    private var currentTimeLeft: Long = 0
    private var row: Int = 1
    private var col: Int = 1
    private var gameTimeLimit: Long = 2000
    private lateinit var currentLevel: String
    private val gameArgs: PlayRememberTheCardGameFragmentArgs by navArgs()
    private var isCardTimerShowing = true
    private var isAllPairsFormed = false
    private var model = ArrayList<RememberTheCardData>()
    private var timeSpentInEndless: Long = 0
    private var infTime = 9999999999L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.i("onViewCreated called")
        super.onViewCreated(view, savedInstanceState)
        requireActivity().findViewById<ConstraintLayout>(R.id.header).visibility = View.GONE
        navController =
            Navigation.findNavController(requireActivity(), R.id.rem_the_card_nav_host_fragment)
        initGameParameters()
        initViews()
    }

    private fun initViews() {
        Timber.i("initViews called")
        rem_card_level_tv.text = getString(R.string.current_level, currentLevel)
        msg_tv.text =
            getString(R.string.you_have_s_seconds_to_remember_the_cards, cardVisibleTime.toString())
        val listOfRules: ArrayList<RememberTheCardData> = getRecyclerViewDataForCards()
        if (listOfRules.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "There was an issue while loading game. Please try again",
                Toast.LENGTH_LONG
            ).show()
            navController.navigate(R.id.action_playRememberTheCardGameFragment_to_rememberTheCardMenuFragment)
        } else {
            model = listOfRules
            rememberCardView = RememberCardView(requireContext(), listOfRules, col, this)
            game_container.addView(rememberCardView)
            setBackButtonHandling()
            if (isEndless) {
                setEndlessGameProperties()
            }
            startTimer((cardVisibleTime * 1000).toLong())
        }
    }

    fun checkIfMatched(rememberTheCardData: RememberTheCardData, position: Int) {
        Timber.i("checkIfMatched called")
        currentMoves++
        live_moves_tv.text = currentMoves.toString()
        if (rememberTheCardData.id == elementToAsk.id) {
            model.remove(rememberTheCardData)
            rememberCardView.showCardAt(position)
            setEndlessGameProperties()
        } else {
            rememberCardView.hideCardAt(position)
            Toast.makeText(requireContext(), "Wrong card picked", Toast.LENGTH_LONG).show()
        }
    }

    private fun setEndlessGameProperties() {
        Timber.i("setEndlessGameProperties called")
        if (model.size == 0) {
            showGameCompletePopup()
            return
        }
        elementToAsk =
            if (model.size == 1) {
                model[0]
            } else {
                val random = Random.nextInt(0, model.size)
                model[random]
            }
    }

    private fun initGameParameters() {
        Timber.i("initGameParameters called")
        isEndless = gameArgs.isEndless
        row = gameArgs.row
        col = gameArgs.col
        gameTimeLimit = gameArgs.timeLimit
        currentLevel = gameArgs.level
        cardVisibleTime = gameArgs.cardVisibleTime
        gameName = gameArgs.gameName
    }

    private fun getRecyclerViewDataForCards(): ArrayList<RememberTheCardData> {
        Timber.i("getDrawablesForCard called")
        val list = ArrayList<RememberTheCardData>()
        val listOfDrawable = getTheRandomImagesList()
        Timber.i("size of drawables of filtered list ${listOfDrawable.size}")
        if (listOfDrawable.size != (row * col) / 2) {
            return arrayListOf()
        }
        for (i in 0 until (row * col) / 2) {
            list.add(RememberTheCardData(listOfDrawable[i], getRandomString()))
        }
        val doubleList = ArrayList<RememberTheCardData>()
        list.forEach {
            doubleList.add(
                RememberTheCardData(
                    it.drawableRes,
                    it.id,
                    it.isRevealed,
                    it.isLocked,
                    true
                )
            )
        }
        val double = list + doubleList
        return double.shuffled() as ArrayList<RememberTheCardData>
    }

    private fun startTimer(gameTimeLimit: Long) {
        Timber.i("startTimer called")
        timer = object : CountDownTimer(gameTimeLimit, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Timber.i("ticking at ${gameTimeLimit / 1000}")
                currentTimeLeft = millisUntilFinished
                rem_card_timer_tv.text =
                    getString(R.string.time_left, (currentTimeLeft / 1000).toString())

            }

            override fun onFinish() {
                Timber.i("timer onFinish called")
                if (isCardTimerShowing) {
                    rememberCardView.hideAllCards()
                    isCardTimerShowing = false
                    msg_tv.text =
                        "You have ${this@PlayRememberTheCardGameFragment.gameTimeLimit} seconds to find the pair of similar cards."
                    startTimer(this@PlayRememberTheCardGameFragment.gameTimeLimit * 1000)
                } else {
                    if (!isEndless) {
                        showGameCompletePopup()
                    }else{
                        where_card_cont.visibility = View.VISIBLE
                        msg_tv.visibility = View.GONE
                        Glide.with(requireContext()).load(elementToAsk.drawableRes).into(where_card)
                        startTimer(infTime - timeSpentInEndless)
                    }
                }

            }
        }
        timer.start()
    }

    fun increaseMovesCounter() {
        Timber.i("increaseMovesCounter called")
        currentMoves++
        live_moves_tv.text = currentMoves.toString()
    }

    fun updateTime(isGameFinished: Boolean = false) {
        Timber.i("updateTime called")
        if (!isGameFinished) {
            Timber.i("current time spent at ${currentTimeLeft / 1000}")
            timer.cancel()
            currentTimeLeft += 5000
            Timber.i("after increasing by 5 seconds ${currentTimeLeft / 1000}")
            startTimer(currentTimeLeft)
        } else {
            isAllPairsFormed = true
            showGameCompletePopup()
        }
    }

    private fun showGameCompletePopup() {
        Timber.i("showGameCompletePopup called")
        lifecycleScope.launch {
            if (isEndless) {
                /* timer.cancel()
                 viewModel.apply {
                     updateHighScoreForInfinite(
                         GameConstants.FIND_THE_NUMBER_INFINITE_GAME_NAME,
                         gridSize,
                         currentScore,
                         timeSpentInEndless / 1000
                     )
                     updateTotalGamePlayed(GameConstants.FIND_THE_NUMBER_GAME_NAME)
                     updateTotalTimePlayed(GameConstants.FIND_THE_NUMBER_GAME_NAME,timeSpentInEndless/1000)
                 }*/
            } else {
                viewModel.apply {
                    updateHighScoreIfApplicable(
                        gameName,
                        currentLevel,
                        currentMoves
                    )
                    updateGameLevel(
                        gameName,
                        (currentLevel.toInt() + 1).toString()
                    )
                    updateTotalGamePlayed(gameName)
                    updateTotalTimePlayed(gameName, currentTimeLeft / 1000)
                }
            }
        }
        val dialogData = if (isEndless) DialogData(
            title = getString(R.string.rem_the_card_game_complete_title),
            content = getString(
                R.string.rem_the_card_game_complete_content,
                currentMoves.toString()
            ),
            posBtnText = getString(R.string.game_complete_positive_btn_txt),
            negBtnText = getString(R.string.game_exit_positive_btn_txt),
            posListener = {
                setNextLevel()
            },
            megListener = {
                if (::navController.isInitialized) {
                    navController.navigate(R.id.action_playRememberTheCardGameFragment_to_rememberTheCardMenuFragment)
                }
            }
        ) else {
            val title: String
            val content: String
            val posBtnText: String
            val negBtnText: String
            val posListener: () -> Unit
            val negListener: () -> Unit
            if (isAllPairsFormed) {
                title = getString(R.string.rem_the_card_game_complete_title)
                content = getString(
                    R.string.rem_the_card_game_complete_content,
                    currentMoves.toString()
                )
                posBtnText = getString(R.string.game_complete_positive_btn_txt)
                negBtnText = getString(R.string.game_exit_positive_btn_txt)
                posListener = {
                    if (::navController.isInitialized) {
                        navController.navigate(R.id.action_playRememberTheCardGameFragment_self)
                        setNextLevel()
                    }
                }
                negListener = { exitGame() }
            } else {
                title = getString(R.string.game_complete_title)
                content = "Your time is up and all pairs are not found. Try again ?"
                posBtnText = getString(R.string.game_retry_btn_txt)
                negBtnText = getString(R.string.game_exit_positive_btn_txt)
                posListener = { retryGame() }
                negListener = {
                    if (::navController.isInitialized) {
                        navController.navigate(R.id.action_playRememberTheCardGameFragment_to_rememberTheCardMenuFragment)
                    }
                }
            }
            DialogData(
                title = title,
                content = content,
                posBtnText = negBtnText,
                negBtnText = posBtnText,
                posListener = negListener,
                megListener = posListener
            )
        }
        timer.cancel()
        val instance = ConfirmationDialog.newInstance(dialogData)
        instance.isCancelable = false
        instance.show(parentFragmentManager, AppConstants.GAME_COMPLETE_TAG)
    }

    private fun retryGame() {
        Timber.i("retryGame called")
        val action =
            PlayRememberTheCardGameFragmentDirections.actionPlayRememberTheCardGameFragmentSelf(
                row = row,
                col = col,
                timeLimit = gameTimeLimit,
                level = currentLevel, isEndless = false,
                cardVisibleTime = cardVisibleTime,
                gameName = gameName
            )
        navController.navigate(action)
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
                    /*if (isEndless) startTimer(infTime-timeSpentInEndless)
                    else */startTimer(currentTimeLeft)
                }
            )
            timer.cancel()
            val instance = ConfirmationDialog.newInstance(dialogData)
            instance.isCancelable = false
            instance.show(parentFragmentManager, AppConstants.GAME_EXIT_TAG)
        }
    }

    private fun setNextLevel() {
        Timber.i("setNextLevel called")
        val rulesJson = (requireActivity() as AppCompatActivity).readJsonFromAsset(
            AppConstants.REMEMBER_CARDS_GAME_RULE_FILE_NAME
        )
        val json = JSONObject(rulesJson)
        val obj =if(isEndless){
            Gson().fromJson(json.getJSONArray("endless").toString(), Array<RememberTheCardGameRule>::class.java)
        }else{
            Gson().fromJson(json.getJSONArray("time-bound").toString(), Array<RememberTheCardGameRule>::class.java)
        }
        val gameRule = obj[currentLevel.toInt()]
        gameTimeLimit = 0L
        timer.cancel()
        val action =
            PlayRememberTheCardGameFragmentDirections.actionPlayRememberTheCardGameFragmentSelf(
                row = gameRule.row,
                col = gameRule.col,
                timeLimit = gameRule.timeLimit,
                level = gameRule.level, isEndless = isEndless,
                cardVisibleTime = cardVisibleTime,
                gameName = gameName
            )
        navController.navigate(action)
    }

    private fun exitGame(it: DialogFragment? = null) {
        Timber.i("exitGame called")
        navController.navigate(R.id.action_playRememberTheCardGameFragment_to_rememberTheCardMenuFragment)
        it?.dismiss()
    }

    private fun getTheRandomImagesList(): ArrayList<Bitmap> {
        Timber.i("getTheRandomImagesList called")
        val listOfDrawables = ArrayList<Bitmap>()
        val imagesNameList = (arrayOfImages.shuffled() as ArrayList)
        for (i in 0 until (row * col) / 2) {
            listOfDrawables.apply {
                with(requireContext()) {
                    assetToBitmap(imagesNameList[i])?.let { add(it) }
                }
            }
        }
        return listOfDrawables.shuffled() as ArrayList<Bitmap>
    }
}