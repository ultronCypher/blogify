export const getPreviewImage = (url) => {
    if (!url) return null;
    return url.replace(
        "/upload/",
        "/upload/w_600,h_350,c_fill,q_auto,f_auto/"
    );
};
