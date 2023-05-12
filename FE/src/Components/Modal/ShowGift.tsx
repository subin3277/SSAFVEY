// 사용된 any는 사용자 타입 가드를 위한 것이에요
/* eslint-disable @typescript-eslint/no-explicit-any */
import React, { useEffect, useRef } from 'react';
import style from './BuyGift.module.css';
import { putGift } from '../../Api/member';
import { queryClient } from '@/router';

interface Props {
  closemodal: () => void;
  info : iteminfo;
}

interface iteminfo {
  id: number;
  name: string;
  imageUrl: string;
}

const isMouseEvent = (e: any): e is MouseEvent =>
  e.type === 'mousedown' || e.type === 'mouseend' || e.type === 'mousemove';
const isTouchEvent = (e: any): e is TouchEvent =>
  e.type === 'touchstart' || e.type === 'touchend' || e.type === 'touchmove';

export default function ShowGift({ closemodal, info }: Props) {

  const slideRef = useRef<HTMLDivElement | null>(null);
  const slideBtnRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    let active: boolean | 'touch' | 'mouse' = false;
    let currentX: number;
    let initialX: number;
    let xOffset = 0;

    const token = queryClient.getQueryData(['accessToken']) as string;

    async function putitem() {
      try {
        if (info.id) {
          await putGift(token);
        }
      } catch (error) {
        console.error(error);
      }
    }

    const setTranslate = (xPos: number, el: HTMLDivElement) => {
      let x = xPos;
      if (xPos < 0) {
        x = 0;
      } else if (xPos >= 170) {
        x = 170;
      }

      el.setAttribute('style', `transform : translatex(${x}px)`);
    };

    const dragStart = (e: MouseEvent | TouchEvent) => {
      if (isTouchEvent(e)) {
        initialX = e.touches[0].clientX - xOffset;
        active = 'touch';
      } else if (isMouseEvent(e)) {
        initialX = e.clientX - xOffset;
        active = 'mouse';
      } else throw Error('유효한 이벤트가 아닙니다.');
    };

    const dragEnd = () => {
      if (!slideBtnRef.current || !slideRef.current) return;

      if (currentX < 130) {
        setTranslate(0, slideBtnRef.current);
        xOffset = 0;
      } else if (currentX >= 130) {
        setTranslate(170, slideBtnRef.current);
        putitem();
        slideRef.current.removeEventListener('touchstart', dragStart);
        slideRef.current.removeEventListener('touchend', dragEnd);
        slideRef.current.removeEventListener('touchmove', drag);

        slideRef.current.removeEventListener('mousedown', dragStart);
        slideRef.current.removeEventListener('mouseup', dragEnd);
        slideRef.current.removeEventListener('mousemove', drag);
        xOffset = 170;
      }

      initialX = currentX;

      if (xOffset === 170) slideRef.current.setAttribute('class', style.buygift);
      else slideRef.current.setAttribute('class', style.slider);

      active = false;
    };

    const drag = (e: MouseEvent | TouchEvent) => {
      if (!slideBtnRef.current || !slideRef.current) return;

      if (active) {
        e.preventDefault();
        if (isTouchEvent(e) && active === 'touch') currentX = e.touches[0].clientX - initialX;
        else if (isMouseEvent(e) && active === 'mouse') currentX = e.clientX - initialX;

        xOffset = currentX;
        setTranslate(currentX, slideBtnRef.current);
      }
    };
    if (slideBtnRef.current && slideRef.current && token) {
      slideRef.current.addEventListener('touchstart', dragStart);
      slideRef.current.addEventListener('touchend', dragEnd);
      slideRef.current.addEventListener('touchmove', drag);

      slideRef.current.addEventListener('mousedown', dragStart);
      slideRef.current.addEventListener('mouseup', dragEnd);
      slideRef.current.addEventListener('mousemove', drag);
    }
  }, [info]);

  return (
    <div>
      <div className={style.exitbtn}>
        <button type="button" className="flex" onClick={() => closemodal()}>
          X
        </button>
      </div>
      <div className={style.giftimage}>
        <img src={info.imageUrl} alt="giftcon" />
      </div>
      <div className={style.gifttitle}>{info.name}</div>

      {/* 우리의 리액트는 PWA로써, 스마트폰에서 터치 방식이 주이기에 필요없다고 생각함 */}
      {/* eslint-disable-next-line jsx-a11y/no-static-element-interactions */}
      <div className={style.slider} id="slide" ref={slideRef}>
        <div className={style.moveslider} id="slidebtn" ref={slideBtnRef}>
          사용하기
        </div>
        <span>밀어서 사용하기</span>
      </div>
    </div>
  );
}
