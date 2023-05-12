import React from 'react';
import { LoaderFunctionArgs, Outlet, useLoaderData } from 'react-router-dom';
import { QueryClient } from '@tanstack/react-query';
import SurveyHeader from '../Components/Survey/SurveyHeader';
import { getStart } from '../Api/survey';
import { SurveyCoverData } from '../types/surveyType';

export default function Survey() {
  const surveyCoverResData = useLoaderData() as SurveyCoverData;
  return (
    <article className="text-white">
      <SurveyHeader
        title={surveyCoverResData.title}
        creator={surveyCoverResData.creator}
        endDate={surveyCoverResData.endDate}
      />
      <main>
        <Outlet context={{ surveyCoverResData }} /> {/* 여기에 SurveyCover, SurveyQuestion가 들어감 */}
      </main>
    </article>
  );
}

export const loader =
  (queryClient: QueryClient) =>
  async ({ params }: LoaderFunctionArgs) => {
    const { id } = params;
    if (!id) return 0;
    let accessToken: string =
      queryClient.getQueryData(['accessToken']) ?? (await queryClient.fetchQuery(['accessToken'], async () => 'tmp'));
    if (accessToken == null) accessToken = 'tmp';
    const data = await getStart(id, accessToken);
    return data;
  };
