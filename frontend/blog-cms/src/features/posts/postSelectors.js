export const selectPosts = (state) => state.posts.posts;
export const selectCurrentPage = (state) => state.posts.currentPage;
export const selectTotalPages = (state) => state.posts.totalPages;
export const selectTotalElements = (state) => state.posts.totalElements;
export const selectPageSize = (state) => state.posts.pageSize;
export const selectPostsStatus = (state) => state.posts.status;
export const selectPostsError = (state) => state.posts.error;
