import { useState } from 'react';
import { SurveyPostRequestData, SurveyQuestionData, Answer } from '@/types/surveyType';
import { go, map } from '@/module/fx';
import { postAnswer } from '@/Api/survey';
import { answersState } from '@/Store/Create/atom';

export interface AnswerObj {
  [id: number]: number | string;
}

export const useSurveyQuestionDataParser = (
  surveyQuestionData: SurveyQuestionData,
  id: number,
): [AnswerObj, React.Dispatch<React.SetStateAction<AnswerObj>>, any] => {
  const answerObj: AnswerObj = {};
  const addObjectKey = ({ isMultipleChoice, order }: { isMultipleChoice: boolean; order: number }) => {
    answerObj[order] = isMultipleChoice ? 0 : '';
    return answerObj[order];
  };

  go(surveyQuestionData.surveyQuestions, map(addObjectKey));

  const [answerStateObj, setAnswerStateObj] = useState(answerObj);

  console.log(answerStateObj);

  const submitAnswer = async (token: string) => {
    const requestData: SurveyPostRequestData = {
      surveyId: id,
      answers: [],
    };
    const answers: Answer[] = go(
      Object.entries(answerStateObj),
      map(([key, value]: [number, string | number]) => {
        let answer: Answer = {
          order: Number(key),
          isMultipleChoice: false,
        };
        if (typeof value === 'string') {
          answer = {
            ...answer,
            isMultipleChoice: false,
            answer: value,
          };
        } else {
          answer = {
            ...answer,
            isMultipleChoice: true,
            chosenOrder: value,
          };
        }

        return answer;
      }),
    );
    requestData.answers = answers;
    console.log(requestData);
    const res = await postAnswer(requestData, token);
  };

  return [answerStateObj, setAnswerStateObj, submitAnswer];
};
