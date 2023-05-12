import React from 'react';
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
  kakaoshare: any;
  surveyCoverResData: SurveyCoverData;
}) {
  return (
    <section className={style.buttons}>
      <div style={{ width: '30px' }} />
      <button type="button" className={style.startSurveyBtn}>
        설문 참여
      </button>
      <button type="button" onClick={() => kakaoshare(surveyCoverResData)}>
        <img id="sharing-btn" src="/icons/share.svg" alt="share-icon" />
      </button>
    </section>
  );
}

SurveyIndexCover.Description = Description;
SurveyIndexCover.SurveyInfoWrapper = SurveyInfoWrapper;
SurveyIndexCover.SurveyBtnWrapepr = SurveyBtnWrapepr;

export default SurveyIndexCover;