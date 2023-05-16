import React, { useCallback, useEffect, useRef, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import Modal from 'react-modal';
import MyPageCard from '../Components/MyPage/MyPageCard';
import MyPageCover from '../Components/MyPage/MyPageCover';
import Lotto from '../Components/Modal/Lotto';
import { getMypage, getSurveyResponse, getSurvey, getLogout, getGift, getPointlist, getRefresh } from '../Api/member';
import styles from './MyPage.module.css';
import { queryClient } from '../router';
import { SurveyHistoryObj } from '../types/myPageType';
import { useTokenQuery } from '@/hooks/useTokenQuery';

interface survey {
  title: string;
  name: string;
  id?: number;
}

interface myinfo {
  name: string;
  point: number;
  dosurvey: number;
  makesurvey: number;
  recent: survey[];
  coupon: number;
  numOrder: number;
}

export default function MyPage() {
  const { state } = useLocation();
  console.log(state);
  const time = useRef(state);
  console.log(state, time);

  useEffect(() => {
    if (time.current !== state) {
      setOpenModalFlag(false);
      setLottomodal(false);
    }
  }, [state, time]);

  const navigate = useNavigate();

  const [info, setInfo] = useState<myinfo>({
    name: '',
    point: 0,
    dosurvey: 0,
    makesurvey: 0,
    recent: [],
    coupon: 0,
    numOrder: 0,
  });

  const [dosurvey, setDosurvey] = useState<SurveyHistoryObj>({});
  const [makesurvey, setMakesurvey] = useState<SurveyHistoryObj>({});
  const [pointlist, setPointlist] = useState({});
  const [orderlist, setOrderlist] = useState([]);

  const [openModalFlag, setOpenModalFlag] = useState<'응답한' | '제작한' | '쿠폰' | '포인트' | boolean>(false);
  const [send, setSend] = useState(false);
  const [activityData, setActivityData] = useState<survey[]>([]);

  const [lottomodal, setLottomodal] = useState(false);

  const tokenQuery = useTokenQuery({
    onError: () => {
      localStorage.setItem('refreshToken', '');
      navigate('/sign-in');
    },
    onSuccess: (accessToken) => {
      if (accessToken) fetchAll(accessToken);
      else {
        localStorage.setItem('refreshToken', '');
        navigate('/sign-in');
      }
    },
  });

  const fetchAll = useCallback(async (accessToken: string) => {
    const mypageinfo = await getmypageinfo();
    const giftcon = await getGiftcon(accessToken);
    const pointlistdata = await getPointlistdata(accessToken);
    const dosurveylist = await getdosurveylist(accessToken);
    const makesurveylist = await getmakesurveylist(accessToken);

    await Promise.all([mypageinfo, giftcon, pointlistdata, dosurveylist, makesurveylist]);
  }, []);

  async function getmypageinfo() {
    const accessToken = queryClient.getQueryData(['accessToken']) as string;
    const data = await getMypage(accessToken);
    setInfo({
      name: data.name,
      point: data.point,
      dosurvey: data.numSurveyParticipated,
      makesurvey: data.numSurveyCreated,
      recent: data.recentActivity,
      coupon: data.couponCount,
      numOrder: data.numOrder,
    });

    setActivityData(data.recentActivity);
  }

  const navigateToSignup = (data: object) => {
    navigate('/sign-up', { state: { data } });
  };

  useEffect(() => {
    const refreshToken = localStorage.getItem('refreshToken');
    if (info.name) {
      /* empty */
    } else if (!refreshToken) navigate('/sign-in');
    else if (tokenQuery.data && !tokenQuery.isFetchedAfterMount) fetchAll(tokenQuery.data);
  }, [fetchAll, info.name, navigate, tokenQuery]);

  async function getdosurveylist(accessToken: string) {
    const data = await getSurveyResponse(accessToken);
    setDosurvey(data);
  }

  async function getmakesurveylist(accessToken: string) {
    const data = await getSurvey(accessToken);
    setMakesurvey(data);
  }

  async function getGiftcon(accessToken: string) {
    const data = await getGift(accessToken);
    setOrderlist(data);
  }

  async function getPointlistdata(accessToken: string) {
    const data = await getPointlist(accessToken);
    setPointlist(data);
  }

  async function logout(accessToken: string) {
    await getLogout(accessToken);
    localStorage.setItem('refreshToken', '');
    queryClient.setQueryData(['accessToken'], null);
    navigate('/');
  }

  return (
    <section className={styles.MyPageWrapper}>
      {openModalFlag ? (
        <button
          type="button"
          onClick={() => {
            setSend(true);
            setTimeout(() => {
              setSend(false);
              setOpenModalFlag(false);
            }, 1000);
          }}
        >
          <article className={styles.nameIconWrapper}>
            <h1 className={styles.nameDiv}>{info?.name}님</h1>
            <img src="./icons/settings.svg" alt="settings" />
          </article>
        </button>
      ) : (
        <article className={styles.nameIconWrapper}>
          <h1 className={styles.nameDiv}>{info?.name}님</h1>
          <img src="./icons/settings.svg" alt="settings" />
          <div className={styles.hoverbtn}>
            <button
              type="button"
              className={styles.logout}
              onClick={() => {
                if (tokenQuery.data) logout(tokenQuery.data);
                else
                  getRefresh(localStorage.getItem('refreshToken')).then(({ Authorization }) => logout(Authorization));
              }}
            >
              로그아웃
            </button>
            <button
              type="button"
              className={styles.modify}
              onClick={() => {
                navigate('/sign-up', { state: { data: null } });
              }}
            >
              회원정보수정
            </button>
          </div>
        </article>
      )}

      {typeof openModalFlag === 'string' && openModalFlag === '제작한' && (
        <MyPageCover
          closemodal={() => {
            setOpenModalFlag(false);
          }}
          sending={send}
          contentType="설문"
          content={{
            quantity: info.makesurvey,
            infoType: openModalFlag,
            renderingData: makesurvey,
          }}
        />
      )}

      {typeof openModalFlag === 'string' && openModalFlag === '응답한' && (
        <MyPageCover
          closemodal={() => {
            setOpenModalFlag(false);
          }}
          sending={send}
          contentType="설문"
          content={{
            quantity: info.dosurvey,
            infoType: openModalFlag,
            renderingData: dosurvey,
          }}
        />
      )}

      {typeof openModalFlag === 'string' && openModalFlag === '쿠폰' && (
        <MyPageCover
          closemodal={() => setOpenModalFlag(false)}
          sending={send}
          contentType="쿠폰"
          content={{
            quantity: info.numOrder,
            infoType: openModalFlag,
            renderingData: orderlist,
          }}
        />
      )}
      {typeof openModalFlag === 'string' && openModalFlag === '포인트' && (
        <MyPageCover
          closemodal={() => setOpenModalFlag(false)}
          sending={send}
          contentType="포인트"
          content={{
            quantity: info.point,
            infoType: openModalFlag,
            renderingData: pointlist,
          }}
        />
      )}

      <div className={styles.contentWrapper}>
        <article className={styles.cardWrapper}>
          <MyPageCard tag="포인트" quantity={info.point} modalOpenFunc={setOpenModalFlag} />
          <MyPageCard tag="참여설문" quantity={info.dosurvey} modalOpenFunc={setOpenModalFlag} />
          <MyPageCard tag="제작설문" quantity={info.makesurvey} modalOpenFunc={setOpenModalFlag} />
        </article>
        <article className={styles.recentDiv}>
          <div className={styles.recentDivImgWrapper}>
            <h3 className={styles.recentTitle}>최근 활동</h3>
            <img src="./icons/reverse_clock.svg" alt="reverse_clock" className={styles.recentImg} />
          </div>
          <div className={styles.recentActivityWrapper}>
            {activityData?.map((activity, idx) => (
              <button
                type="button"
                onClick={() => {
                  navigate(`/survey/${activity.id}`);
                }}
              >
                <div
                  // 데이터를 나타내는 것이 중요하기에,
                  // eslint-disable-next-line react/no-array-index-key
                  key={idx}
                  className={styles.recentActivityBg}
                >
                  <div className={styles.title}>{activity.title}</div>
                  <div className={styles.author}>{activity.name}</div>
                </div>
              </button>
            ))}
          </div>
        </article>
        <button type="button" onClick={() => setOpenModalFlag('쿠폰')}>
          <article className={styles.couponBox}>
            <h3 className={styles.couponText}>보유한 쿠폰</h3>
            <p className={styles.couponCntDiv}>{info.numOrder}</p>
          </article>
        </button>
        {info.coupon > 0 ? (
          <button type="button" onClick={() => setLottomodal(true)}>
            <article className={styles.couponBox}>
              <h3 className={styles.couponText}>로또 사용하기</h3>
              <p className={styles.couponCntDiv}>{info.coupon}</p>
            </article>
          </button>
        ) : (
          <button type="button">
            <article className={styles.couponBox}>
              <h3 className={styles.couponText}>보유한 로또가 없어요</h3>
              <p className={styles.couponCntDiv}>{info.coupon}</p>
            </article>
          </button>
        )}

        <Modal
          // className={style.updatemodal}
          closeTimeoutMS={200}
          isOpen={lottomodal}
          onRequestClose={() => setLottomodal(false)}
          style={{
            content: {
              width: '300px',
              height: '350px',
              backgroundColor: '#c2e9fb',
              margin: 'auto',
              borderRadius: '20px',
            },
          }}
        >
          <Lotto closemodal={() => setLottomodal(false)} />
        </Modal>
      </div>
    </section>
  );
}
