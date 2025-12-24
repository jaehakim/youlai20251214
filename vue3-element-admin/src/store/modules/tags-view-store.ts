export const useTagsViewStore = defineStore("tagsView", () => {
  const visitedViews = 참조<TagView[]>([]);
  const cachedViews = 참조<string[]>([]);
  const router = useRouter();
  const route = useRoute();

  /**
   * 추가已访问뷰到已访问뷰목록내
   */
  function addVisitedView(view: TagView) {
    // 이미存에于已访问의뷰목록내或者是重定에地址，그러면不再추가
    if (view.path.startsWith("/redirect")) {
      return;
    }
    if (visitedViews.value.some((v) => v.path === view.path)) {
      return;
    }
    // 만약뷰是固定의（affix），그러면에已访问의뷰목록의开头추가
    if (view.affix) {
      visitedViews.value.unshift(view);
    } else {
      // 만약뷰不是固定의，그러면에已访问의뷰목록의末尾추가
      visitedViews.value.push(view);
    }
  }

  /**
   * 추가캐시뷰到캐시뷰목록내
   */
  function addCachedView({ fullPath, keepAlive }: TagView) {
    // 만약캐시뷰이름칭已经存에于캐시뷰목록내，그러면不再추가
    if (cachedViews.value.includes(fullPath)) {
      return;
    }

    // 만약뷰필요해야캐시（keepAlive），그러면로其라우팅이름칭추가到캐시뷰목록내
    if (keepAlive) {
      cachedViews.value.push(fullPath);
    }
  }

  /**
   * 从已访问뷰목록내삭제指定의뷰
   */
  function delVisitedView(view: TagView) {
    return new Promise((resolve) => {
      for (const [i, v] of visitedViews.value.entries()) {
        // 找到与指定뷰경로일치의뷰，에已访问뷰목록내삭제该뷰
        if (v.path === view.path) {
          visitedViews.value.splice(i, 1);
          break;
        }
      }
      resolve([...visitedViews.value]);
    });
  }

  function delCachedView(view: TagView) {
    const { fullPath } = view;
    return new Promise((resolve) => {
      const index = cachedViews.value.indexOf(fullPath);
      if (index > -1) {
        cachedViews.value.splice(index, 1);
      }
      resolve([...cachedViews.value]);
    });
  }
  function delOtherVisitedViews(view: TagView) {
    return new Promise((resolve) => {
      visitedViews.value = visitedViews.value.filter((v) => {
        return v?.affix || v.path === view.path;
      });
      resolve([...visitedViews.value]);
    });
  }

  function delOtherCachedViews(view: TagView) {
    const { fullPath } = view;
    return new Promise((resolve) => {
      const index = cachedViews.value.indexOf(fullPath);
      if (index > -1) {
        cachedViews.value = cachedViews.value.slice(index, index + 1);
      } else {
        // if index = -1, there is no cached tags
        cachedViews.value = [];
      }
      resolve([...cachedViews.value]);
    });
  }

  function updateVisitedView(view: TagView) {
    for (let v of visitedViews.value) {
      if (v.path === view.path) {
        v = Object.assign(v, view);
        break;
      }
    }
  }

  /**
   * 에 따라경로업데이트标签이름칭
   * @param fullPath 경로
   * @param title 标签이름칭
   */
  function updateTagName(fullPath: string, title: string) {
    const tag = visitedViews.value.find((tag: TagView) => tag.fullPath === fullPath);

    if (tag) {
      tag.title = title;
    }
  }

  function addView(view: TagView) {
    addVisitedView(view);
    addCachedView(view);
  }

  function delView(view: TagView) {
    return new Promise((resolve) => {
      delVisitedView(view);
      delCachedView(view);
      resolve({
        visitedViews: [...visitedViews.value],
        cachedViews: [...cachedViews.value],
      });
    });
  }

  function delOtherViews(view: TagView) {
    return new Promise((resolve) => {
      delOtherVisitedViews(view);
      delOtherCachedViews(view);
      resolve({
        visitedViews: [...visitedViews.value],
        cachedViews: [...cachedViews.value],
      });
    });
  }

  function delLeftViews(view: TagView) {
    return new Promise((resolve) => {
      const currIndex = visitedViews.value.findIndex((v) => v.path === view.path);
      if (currIndex === -1) {
        return;
      }
      visitedViews.value = visitedViews.value.filter((item, index) => {
        if (index >= currIndex || item?.affix) {
          return true;
        }

        const cacheIndex = cachedViews.value.indexOf(item.fullPath);
        if (cacheIndex > -1) {
          cachedViews.value.splice(cacheIndex, 1);
        }
        return false;
      });
      resolve({
        visitedViews: [...visitedViews.value],
      });
    });
  }

  function delRightViews(view: TagView) {
    return new Promise((resolve) => {
      const currIndex = visitedViews.value.findIndex((v) => v.path === view.path);
      if (currIndex === -1) {
        return;
      }
      visitedViews.value = visitedViews.value.filter((item, index) => {
        if (index <= currIndex || item?.affix) {
          return true;
        }
        const cacheIndex = cachedViews.value.indexOf(item.fullPath);
        if (cacheIndex > -1) {
          cachedViews.value.splice(cacheIndex, 1);
        }
        return false;
      });
      resolve({
        visitedViews: [...visitedViews.value],
      });
    });
  }

  function delAllViews() {
    return new Promise((resolve) => {
      const affixTags = visitedViews.value.filter((tag) => tag?.affix);
      visitedViews.value = affixTags;
      cachedViews.value = [];
      resolve({
        visitedViews: [...visitedViews.value],
        cachedViews: [...cachedViews.value],
      });
    });
  }

  function delAllVisitedViews() {
    return new Promise((resolve) => {
      const affixTags = visitedViews.value.filter((tag) => tag?.affix);
      visitedViews.value = affixTags;
      resolve([...visitedViews.value]);
    });
  }

  function delAllCachedViews() {
    return new Promise((resolve) => {
      cachedViews.value = [];
      resolve([...cachedViews.value]);
    });
  }

  /**
   * 닫기当前tagView
   */
  function closeCurrentView() {
    const tags: TagView = {
      name: route.name as string,
      title: route.meta.title as string,
      path: route.path,
      fullPath: route.fullPath,
      affix: route.meta?.affix,
      keepAlive: route.meta?.keepAlive,
      query: route.query,
    };
    delView(tags).then((res: any) => {
      if (isActive(tags)) {
        toLastView(res.visitedViews, tags);
      }
    });
  }

  function isActive(tag: TagView) {
    return tag.path === route.path;
  }

  function toLastView(visitedViews: TagView[], view?: TagView) {
    const latestView = visitedViews.slice(-1)[0];
    if (latestView && latestView.fullPath) {
      router.push(latestView.fullPath);
    } else {
      // now the default is to redirect to the home page if there is no tags-view,
      // you can adjust it according to your needs.
      if (view?.name === "Dashboard") {
        // to reload home page
        router.replace("/redirect" + view.fullPath);
      } else {
        router.push("/");
      }
    }
  }

  return {
    visitedViews,
    cachedViews,
    addVisitedView,
    addCachedView,
    delVisitedView,
    delCachedView,
    delOtherVisitedViews,
    delOtherCachedViews,
    updateVisitedView,
    addView,
    delView,
    delOtherViews,
    delLeftViews,
    delRightViews,
    delAllViews,
    delAllVisitedViews,
    delAllCachedViews,
    closeCurrentView,
    isActive,
    toLastView,
    updateTagName,
  };
});
