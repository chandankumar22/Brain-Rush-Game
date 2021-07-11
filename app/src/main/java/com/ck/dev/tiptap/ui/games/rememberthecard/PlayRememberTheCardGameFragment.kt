package com.ck.dev.tiptap.ui.games.rememberthecard

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.extensions.fetchDrawable
import com.ck.dev.tiptap.helpers.AppConstants
import com.ck.dev.tiptap.helpers.readJsonFromAsset
import com.ck.dev.tiptap.models.DialogData
import com.ck.dev.tiptap.models.RememberTheCardData
import com.ck.dev.tiptap.models.RememberTheCardGameRule
import com.ck.dev.tiptap.ui.custom.RememberCardView
import com.ck.dev.tiptap.ui.dialogs.ConfirmationDialog
import com.ck.dev.tiptap.ui.games.BaseFragment
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_play_jumbled_words_game.*
import kotlinx.android.synthetic.main.fragment_play_remember_the_card_game.*
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.random.Random

class PlayRememberTheCardGameFragment :
    BaseFragment(R.layout.fragment_play_remember_the_card_game) {

    private lateinit var gameName: String
    private val currentScore: Int = 0
    private var cardVisibleTime: Int = 0
    private lateinit var rememberCardView: RememberCardView
    private lateinit var navController: NavController
    private lateinit var timer: CountDownTimer
    private var isEndless: Boolean = false
    private var isExit: Boolean = false
    private var currentTimeSpentInGame: Long = 0
    private var row: Int = 1
    private var col: Int = 1
    private var gameTimeLimit: Long = 2000
    private lateinit var currentLevel: String
    private val gameArgs: PlayRememberTheCardGameFragmentArgs by navArgs()
    private var isCardTimerShowing = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().findViewById<ConstraintLayout>(R.id.header).visibility = View.GONE
        navController =
            Navigation.findNavController(requireActivity(), R.id.rem_the_card_nav_host_fragment)
        initGameParameters()
        initViews()
        setBackButtonHandling()
        startTimer((cardVisibleTime * 1000).toLong())
    }

    fun getRandomString(length: Int=30) : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    private fun initViews() {
        rem_card_level_tv.text = getString(R.string.current_level, currentLevel)
        msg_tv.text =
            getString(R.string.you_have_s_seconds_to_remember_the_cards, cardVisibleTime.toString())
        val listOfRules: ArrayList<RememberTheCardData> = getDrawablesForCard()
        rememberCardView = RememberCardView(requireContext(), listOfRules, col,this)
        game_container.addView(rememberCardView)
    }

    private fun initGameParameters() {
        isEndless = gameArgs.isEndless
        row = gameArgs.row
        col = gameArgs.col
        gameTimeLimit = gameArgs.timeLimit
        currentLevel = gameArgs.level
        cardVisibleTime = gameArgs.cardVisibleTime
        gameName = gameArgs.gameName
    }

    private fun getDrawablesForCard(): ArrayList<RememberTheCardData> {
        val list = ArrayList<RememberTheCardData>()
        val listOfDrawable = getTheRandomDrawableList()
        /*var randomStart = Random.nextInt(0,listOfDrawable.size/2)
        var randomEnd = Random.nextInt(randomStart+1,listOfDrawable.size)
        if(randomStart==randomEnd){
            Timber.i("by chance random start and random end to slice random list of drawable is same")
             randomStart =  Random.nextInt(0,listOfDrawable.size/2)
            randomEnd = Random.nextInt(randomStart+1,listOfDrawable.size)
        }
        Timber.i("slicing list from $randomStart to $randomEnd")
        val filterList =listOfDrawable.slice(randomStart..randomEnd)*/
        val finalList = ArrayList<Drawable>()
        for(i in 0 until (row*col)/2){
            finalList.add(listOfDrawable[i])
        }
        Timber.i("size of drawables of filtered list ${finalList.size}")
        for (i in 0 until (row * col)/2) {
            list.add(RememberTheCardData(finalList[i], getRandomString()))
        }

        val doubleList = ArrayList<RememberTheCardData>()
        list.forEach {
            doubleList.add(RememberTheCardData(it.drawableRes,it.id,it.isRevealed,it.isLocked,true))
        }

        val double = list+doubleList

        return double.shuffled() as ArrayList<RememberTheCardData>
    }

    private fun startTimer(gameTimeLimit: Long) {
        Timber.i("startTimer called")
        timer = object : CountDownTimer(gameTimeLimit, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                currentTimeSpentInGame = millisUntilFinished
                rem_card_timer_tv.text =
                    getString(R.string.time_left, (millisUntilFinished / 1000).toString())

            }

            override fun onFinish() {
                Timber.i("timer onFinish called")
                if (isCardTimerShowing) {
                    rememberCardView.hideAllCards()
                    isCardTimerShowing = false
                    msg_tv.text = "You have ${this@PlayRememberTheCardGameFragment.gameTimeLimit} seconds to find the pair of similar cards."
                    startTimer(this@PlayRememberTheCardGameFragment.gameTimeLimit * 1000)
                } else {
                    if (!isEndless) showGameCompletePopup()
                }

            }
        }
        timer.start()
    }

    fun updateTime(isGameFinished:Boolean=false){
        if(!isGameFinished){
            timer.cancel()
            currentTimeSpentInGame -= 5000
            startTimer(currentTimeSpentInGame)
        }else{
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
                        currentScore
                    )
                    updateGameLevel(
                        gameName,
                        (currentLevel.toInt() + 1).toString()
                    )
                    updateTotalGamePlayed(gameName)
                    updateTotalTimePlayed(gameName, currentTimeSpentInGame / 1000)
                }
            }
        }
        val dialogData = if (isEndless) DialogData(
            title = getString(R.string.game_complete_infinite_title),
            content = getString(
                R.string.game_complete_infinite_content,
                currentScore.toString(),
                (1/*timeSpentInEndless*/ / 1000).toString()
            ),
            posBtnText = getString(R.string.game_exit_positive_btn_txt),
            negBtnText = getString(R.string.game_retry_btn_txt),
            posListener = {
                if (::navController.isInitialized) {
                    navController.navigate(R.id.action_playRememberTheCardGameFragment_to_rememberTheCardMenuFragment)
                }
            },
            megListener = {
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
        ) else DialogData(
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
                    navController.navigate(R.id.action_playRememberTheCardGameFragment_self)
                    setNextLevel()
                }
            },
            megListener = {
                exitGame()
            }
        )
        val instance = ConfirmationDialog.newInstance(dialogData)
        instance.isCancelable = false
        instance.show(parentFragmentManager, AppConstants.GAME_COMPLETE_TAG)
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
                    else */startTimer(currentTimeSpentInGame)
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
        val obj = Gson().fromJson(rulesJson, Array<RememberTheCardGameRule>::class.java)
        val gameRule = obj[currentLevel.toInt()]
        gameTimeLimit = 0L
        timer.cancel()
        val action =
            PlayRememberTheCardGameFragmentDirections.actionPlayRememberTheCardGameFragmentSelf(
                row = gameRule.row,
                col = gameRule.col,
                timeLimit = gameRule.timeLimit,
                level = gameRule.level, isEndless = false,
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

    private fun getTheRandomDrawableList(): ArrayList<Drawable> {
        val listOfDrawables = ArrayList<Drawable>()
        requireContext().apply {
            listOfDrawables.also {
                it.add(fetchDrawable(R.drawable.air_balloon))
                it.add(fetchDrawable(R.drawable.boat))
                it.add(fetchDrawable(R.drawable.butterfly))
                it.add(fetchDrawable(R.drawable.cap))
                it.add(fetchDrawable(R.drawable.cat))
                it.add(fetchDrawable(R.drawable.chair))
                it.add(fetchDrawable(R.drawable.dice))
                it.add(fetchDrawable(R.drawable.dolphin))
                it.add(fetchDrawable(R.drawable.fan))
                it.add(fetchDrawable(R.drawable.football))
                it.add(fetchDrawable(R.drawable.guitar))
                it.add(fetchDrawable(R.drawable.hammer))
                it.add(fetchDrawable(R.drawable.head_phone))
                it.add(fetchDrawable(R.drawable.helmet))
                it.add(fetchDrawable(R.drawable.horse))
                it.add(fetchDrawable(R.drawable.ladder))
                it.add(fetchDrawable(R.drawable.magnet))
                it.add(fetchDrawable(R.drawable.magnify_glass))
                it.add(fetchDrawable(R.drawable.microphone))
                it.add(fetchDrawable(R.drawable.parachute))
                it.add(fetchDrawable(R.drawable.phone))
                it.add(fetchDrawable(R.drawable.pigeon))
                it.add(fetchDrawable(R.drawable.scissors))
                it.add(fetchDrawable(R.drawable.sim_card))
                it.add(fetchDrawable(R.drawable.wallet))
            }
        }
        return listOfDrawables.shuffled() as ArrayList<Drawable>
    }
}