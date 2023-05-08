import React, { useEffect } from 'react';
import { useSetRecoilState, useRecoilState } from 'recoil';
import { useNavigate } from 'react-router-dom';
import {
  currentQuestionNumberState,
  currentQuestionTitleState,
  currentQuestionTypeState,
  questionsState,
  answersState,
} from '../../../Store/Create/atom';
import style from './CreateSurveyNavigationButtons.module.css';
import CircleButton from '../../../UI/Button/CircleButton';
import RoundButton from '../../../UI/Button/RoundButton';

const START_NO = 1;

export default function CreateSurveyNavigationButtons() {
  const navigate = useNavigate();

  const [currentNumber, setCurrentNumber] = useRecoilState(currentQuestionNumberState);
  const handlePrevButtonClick = () => {
    if (currentNumber === START_NO) {
      navigate('/create/basic');
    } else {
      setCurrentNumber(currentNumber - 1);
      navigate(`/create/${currentNumber - 1}`);
    }
  };

  const setQuestions = useSetRecoilState(questionsState);

  const [currentQuestionTitle, setCurrentQuestionTitle] = useRecoilState(currentQuestionTitleState);

  const [currentQuestionType, setCurrentQuestionType] = useRecoilState(currentQuestionTypeState);

  const [answers, setAnswers] = useRecoilState(answersState);

  const handleNextButtonClick = () => {
    setQuestions((prev) => {
      return [
        ...prev,
        {
          id: currentNumber,
          title: currentQuestionTitle,
          type: currentQuestionType,
          answers,
        },
      ];
    });
    // Todo: 분기처리해야함
    setCurrentNumber(currentNumber + 1);
    setCurrentQuestionTitle('');
    setCurrentQuestionType('multiple');
    setAnswers([]);
    navigate(`/create/${currentNumber + 1}`);
  };

  const handleRouteAdditional = () => {
    navigate('/create/additional');
  };

  return (
    <section className={style.buttons}>
      <CircleButton color="green" size="lg" onClick={handlePrevButtonClick}>
        &lt;
      </CircleButton>
      <RoundButton color="blue" size="lg" onClick={handleRouteAdditional}>
        추가 정보 입력하기
      </RoundButton>
      <CircleButton color="green" size="lg" onClick={handleNextButtonClick}>
        &gt;
      </CircleButton>
    </section>
  );
}
