import React from 'react';
import { useNavigate } from 'react-router-dom';
import writeExcel from '@util/Xlsx/writeExcel';
import { getResult } from '@api/survey';
import { useQueryClient } from '@tanstack/react-query';
import { SurveyCoverData } from '../../types/surveyType';
import style from './SurveyIndexComponent.module.css';

interface SurveyInfoInterface {
  key: string;
  value: string | number;
}

function SurveyIndexCover({ children }: { children: React.ReactNode }) {
  return <div className={style.sectionWrapper}>{children}</div>;
}

function Description({ children }: { children: string }) {
  return (
    <article className={style.descBox}>
      <img src="/icons/about-circle-outline.svg" alt="about-icon" />
      <p>{children}</p>
    </article>
  );
}

function SurveyInfoWrapper({ surveyInfoArr }: { surveyInfoArr: SurveyInfoInterface[] }) {
  console.log(surveyInfoArr);
  return (
    <section className={style.stateBox}>
      {surveyInfoArr.map((surveyInfo) => (
        <dl className={style.childInlineBlock} key={surveyInfo.key}>
          <dt className={style.descTitle}>{surveyInfo.key}</dt>
          <dd>{surveyInfo.value}</dd>
        </dl>
      ))}
    </section>
  );
}

function SurveyBtnWrapepr({
  kakaoshare,
  surveyCoverResData,
}: {
  kakaoshare: (a: SurveyCoverData) => void;
  surveyCoverResData: SurveyCoverData;
}) {
  const queryClient = useQueryClient();

  const accessToken = queryClient.getQueryData(['accessToken']) as string;

  const getStats = async () => {
    // dummyParams는 API에서 받아온 데이터를 넣어주면 됨
    const { surveyQuestionStats } = await getResult(surveyCoverResData.id, accessToken);
    writeExcel(surveyQuestionStats, 5);
  };

  return (
    <section className={style.buttons}>
      <div style={{ width: '30px' }}>
        {surveyCoverResData.isAuthor ? (
          <button type="button" onClick={getStats}>
            통계
          </button>
        ) : (
          <div />
        )}
      </div>
      <StartButton
        isAuthor={surveyCoverResData.isAuthor}
        haveDone={surveyCoverResData.haveDone}
        isDone={surveyCoverResData.isDone}
      />
      <button type="button" onClick={() => kakaoshare(surveyCoverResData)}>
        <img id="sharing-btn" src="/icons/share.svg" alt="share-icon" />
      </button>
    </section>
  );
}

function StartButton({ isAuthor, haveDone, isDone }: { isAuthor: boolean; haveDone: boolean; isDone: boolean }) {
  const navigate = useNavigate();

  // const isDisabled = isAuthor || haveDone || isDone;
  const isDisabled = false;

  const styleClass = isDisabled ? style.cantSurveyBtn : style.startSurveyBtn;

  const getText = () => {
    if (isDone) return '설문 종료';
    if (isAuthor) return '설문 불가';
    if (haveDone) return '설문 완료';
    return '설문 시작';
  };

  const handleClick = () => {
    // if (isDone) return;
    // if (isAuthor) return;
    // if (haveDone) return;
    navigate('doing');
  };

  return (
    <button type="button" className={styleClass} onClick={handleClick}>
      {getText()}
    </button>
  );
}

SurveyIndexCover.Description = Description;
SurveyIndexCover.SurveyInfoWrapper = SurveyInfoWrapper;
SurveyIndexCover.SurveyBtnWrapepr = SurveyBtnWrapepr;

export default SurveyIndexCover;
